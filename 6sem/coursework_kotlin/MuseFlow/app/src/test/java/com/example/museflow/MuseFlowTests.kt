package com.example.museflow

import app.cash.turbine.test
import com.example.museflow.data.local.dao.TrackDao
import com.example.museflow.data.local.entities.TrackEntity
import com.example.museflow.data.network.api.ApiService
import com.example.museflow.data.network.models.TrackDto
import com.example.museflow.data.repository.TracksRepositoryImpl
import com.example.museflow.data.repository.toDomain
import com.example.museflow.domain.models.Track
import com.example.museflow.domain.repository.PlaylistsRepository
import com.example.museflow.domain.usecase.LoginUseCase
import com.example.museflow.domain.usecase.RegisterUseCase
import com.example.museflow.domain.usecase.RemoveTrackFromPlaylistUseCase
import com.example.museflow.presentation.ui.auth.AuthState
import com.example.museflow.presentation.ui.auth.AuthViewModel
import com.example.museflow.presentation.ui.catalog.CatalogState
import com.example.museflow.presentation.ui.catalog.CatalogViewModel
import com.example.museflow.domain.usecase.GetTracksUseCase
import com.example.museflow.domain.usecase.SearchTracksUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class MuseFlowTests {

    private val testDispatcher = kotlinx.coroutines.test.StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // 1. Тест маппинга TrackDto в Track
    @Test
    fun `TrackDto toDomain should map correctly`() {
        val dto = TrackDto(
            id = 1,
            title = "Title",
            artist = "Artist",
            duration = 180,
            coverUrl = "url",
            audioUrl = "audio",
            genre = "Rock"
        )
        val domain = dto.toDomain()

        assertEquals(dto.id, domain.id)
        assertEquals(dto.title, domain.title)
        assertEquals(dto.artist, domain.artist)
        assertEquals(dto.genre, domain.genre)
    }

    // 2. Тест TracksRepositoryImpl - успешное получение данных с сервера
    @Test
    fun `TracksRepositoryImpl getTracks should return tracks from API and cache them`() = runTest {
        val api = mockk<ApiService>()
        val dao = mockk<TrackDao>(relaxed = true)
        val repository = TracksRepositoryImpl(api, dao)
        val tracksDto = listOf(TrackDto(1, "T", "A", 100, "c", "a", "g"))
        
        coEvery { api.getTracks() } returns tracksDto
        
        val result = repository.getTracks()
        
        assertEquals(1, result.size)
        assertEquals("T", result[0].title)
        coVerify { dao.insertAll(any()) }
    }

    // 3. Тест TracksRepositoryImpl - ошибка сети, загрузка из кэша
    @Test
    fun `TracksRepositoryImpl getTracks should return tracks from cache when network fails`() = runTest {
        val api = mockk<ApiService>()
        val dao = mockk<TrackDao>()
        val repository = TracksRepositoryImpl(api, dao)
        val cachedTracks = listOf(TrackEntity(1, "Cached", "A", 100, "c", "a", "g"))
        
        coEvery { api.getTracks() } throws IOException()
        coEvery { dao.getAllTracks() } returns cachedTracks
        
        val result = repository.getTracks()
        
        assertEquals(1, result.size)
        assertEquals("Cached", result[0].title)
    }

    // 4. Тест TracksRepositoryImpl - ошибка сети и пустой кэш
    @Test(expected = Exception::class)
    fun `TracksRepositoryImpl getTracks should throw exception when network fails and cache is empty`() = runTest {
        val api = mockk<ApiService>()
        val dao = mockk<TrackDao>()
        val repository = TracksRepositoryImpl(api, dao)
        
        coEvery { api.getTracks() } throws IOException()
        coEvery { dao.getAllTracks() } returns emptyList()
        
        repository.getTracks()
    }

    // 5. Тест UseCase RemoveTrackFromPlaylistUseCase
    @Test
    fun `RemoveTrackFromPlaylistUseCase should call repository`() = runTest {
        val repository = mockk<PlaylistsRepository>()
        val useCase = RemoveTrackFromPlaylistUseCase(repository)
        
        coEvery { repository.removeTrackFromPlaylist(1, 2) } returns Unit
        
        useCase(1, 2)
        
        coVerify { repository.removeTrackFromPlaylist(1, 2) }
    }

    // 6. Тест AuthViewModel - успешный логин
    @Test
    fun `AuthViewModel login should update state to Success`() = runTest {
        val loginUseCase = mockk<LoginUseCase>()
        val registerUseCase = mockk<RegisterUseCase>()
        val viewModel = AuthViewModel(loginUseCase, registerUseCase)
        
        coEvery { loginUseCase("user", "pass") } returns "fake_token"
        
        viewModel.state.test {
            assertEquals(AuthState.Idle, awaitItem())
            viewModel.login("user", "pass")
            
            // С StandardTestDispatcher нужно продвинуть планировщик
            testScheduler.advanceUntilIdle()

            // Проверим финальное состояние, пропустив промежуточные если они есть.
            var lastState: AuthState = awaitItem()
            while (lastState is AuthState.Loading) {
                lastState = awaitItem()
            }
            assertEquals(AuthState.Success("fake_token"), lastState)
        }
    }

    // 7. Тест AuthViewModel - ошибка логина
    @Test
    fun `AuthViewModel login should update state to Error on failure`() = runTest {
        val loginUseCase = mockk<LoginUseCase>()
        val registerUseCase = mockk<RegisterUseCase>()
        val viewModel = AuthViewModel(loginUseCase, registerUseCase)
        
        coEvery { loginUseCase(any(), any()) } throws Exception("Invalid credentials")
        
        viewModel.state.test {
            assertEquals(AuthState.Idle, awaitItem())
            viewModel.login("user", "wrong")
            
            testScheduler.advanceUntilIdle()
            
            var lastState: AuthState = awaitItem()
            while (lastState is AuthState.Loading) {
                lastState = awaitItem()
            }
            assertTrue(lastState is AuthState.Error)
            assertEquals("Invalid credentials", (lastState as AuthState.Error).message)
        }
    }

    // 8. Тест CatalogViewModel - успешная загрузка треков
    @Test
    fun `CatalogViewModel should load tracks on init`() = runTest {
        val getTracksUseCase = mockk<GetTracksUseCase>()
        val searchTracksUseCase = mockk<SearchTracksUseCase>()
        val tracks = listOf(Track(1, "T", "A", 100, "c", "a", "g"))
        
        coEvery { getTracksUseCase() } returns tracks
        
        val viewModel = CatalogViewModel(getTracksUseCase, searchTracksUseCase)
        
        viewModel.state.test {
            testScheduler.advanceUntilIdle()
            var lastState = awaitItem()
            while (lastState is CatalogState.Loading) {
                lastState = awaitItem()
            }
            assertTrue(lastState is CatalogState.Success)
            assertEquals(tracks, (lastState as CatalogState.Success).tracks)
        }
    }

    // 9. Тест CatalogViewModel - фильтрация по жанрам
    @Test
    fun `CatalogViewModel getTracksByGenre should group tracks correctly`() = runTest {
        val getTracksUseCase = mockk<GetTracksUseCase>()
        val searchTracksUseCase = mockk<SearchTracksUseCase>()
        val tracks = listOf(
            Track(1, "T1", "A1", 100, "c1", "a1", "Rock"),
            Track(2, "T2", "A2", 100, "c2", "a2", "Pop"),
            Track(3, "T3", "A3", 100, "c3", "a3", "Rock")
        )
        
        coEvery { getTracksUseCase() } returns tracks
        val viewModel = CatalogViewModel(getTracksUseCase, searchTracksUseCase)
        
        testScheduler.advanceUntilIdle()
        
        val grouped = viewModel.getTracksByGenre()
        
        assertEquals(2, grouped["Rock"]?.size)
        assertEquals(1, grouped["Pop"]?.size)
    }

    // 10. Тест CatalogViewModel - поиск
    @Test
    fun `CatalogViewModel search should update state with results`() = runTest {
        val getTracksUseCase = mockk<GetTracksUseCase>()
        val searchTracksUseCase = mockk<SearchTracksUseCase>()
        val searchResults = listOf(Track(1, "Found", "A", 100, "c", "a", "g"))
        
        coEvery { getTracksUseCase() } returns emptyList()
        coEvery { searchTracksUseCase("query") } returns searchResults
        
        val viewModel = CatalogViewModel(getTracksUseCase, searchTracksUseCase)
        
        viewModel.state.test {
            testScheduler.advanceUntilIdle()
            // Пропускаем все состояния от инициализации
            while (expectMostRecentItem() is CatalogState.Loading) { awaitItem() }
            
            viewModel.search("query")
            testScheduler.advanceUntilIdle()
            
            var lastState = awaitItem()
            while (lastState is CatalogState.Loading) {
                lastState = awaitItem()
            }
            assertTrue(lastState is CatalogState.Success)
            assertEquals(searchResults, (lastState as CatalogState.Success).tracks)
        }
    }
}
