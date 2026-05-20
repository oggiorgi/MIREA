package org.example.features.prizes

// Данные о премиях (в памяти, не в БД!)
object NobelPrizeData {
    private val prizes = mutableListOf<NobelPrize>()

    init {
        // Заполняем тестовыми данными из реального Nobel Prize API
        prizes.addAll(
            listOf(
                NobelPrize(
                    year = 2023,
                    category = "physics",
                    laureates = listOf(
                        Laureate("agostini", "Pierre Agostini", "for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter", 3),
                        Laureate("krausz", "Ferenc Krausz", "for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter", 3),
                        Laureate("huillier", "Anne L'Huillier", "for experimental methods that generate attosecond pulses of light for the study of electron dynamics in matter", 3)
                    )
                ),
                NobelPrize(
                    year = 2023,
                    category = "chemistry",
                    laureates = listOf(
                        Laureate("bawendi", "Moungi G. Bawendi", "for the discovery and synthesis of quantum dots", 3),
                        Laureate("brus", "Louis E. Brus", "for the discovery and synthesis of quantum dots", 3),
                        Laureate("ekimov", "Alexei I. Ekimov", "for the discovery and synthesis of quantum dots", 3)
                    )
                ),
                NobelPrize(
                    year = 2023,
                    category = "medicine",
                    laureates = listOf(
                        Laureate("kariko", "Katalin Karikó", "for their discoveries concerning nucleoside base modifications that enabled the development of effective mRNA vaccines", 2),
                        Laureate("weissman", "Drew Weissman", "for their discoveries concerning nucleoside base modifications that enabled the development of effective mRNA vaccines", 2)
                    )
                ),
                NobelPrize(
                    year = 2022,
                    category = "physics",
                    laureates = listOf(
                        Laureate("aspect", "Alain Aspect", "for experiments with entangled photons, establishing the violation of Bell inequalities and pioneering quantum information science", 3),
                        Laureate("clauser", "John F. Clauser", "for experiments with entangled photons, establishing the violation of Bell inequalities and pioneering quantum information science", 3),
                        Laureate("zeilinger", "Anton Zeilinger", "for experiments with entangled photons, establishing the violation of Bell inequalities and pioneering quantum information science", 3)
                    )
                )
            )
        )
    }

    fun getAllPrizes(): List<NobelPrize> = prizes.toList()

    fun getPrize(year: Int, category: String): NobelPrize? =
        prizes.find { it.year == year && it.category.equals(category, ignoreCase = true) }

    fun getLaureates(year: Int, category: String): List<Laureate>? =
        getPrize(year, category)?.laureates
}

data class NobelPrize(
    val year: Int,
    val category: String,
    val laureates: List<Laureate>
)

data class Laureate(
    val id: String,
    val fullName: String,
    val motivation: String,
    val share: Int
)