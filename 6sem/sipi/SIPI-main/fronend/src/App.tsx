import { Routes, Route, Navigate } from 'react-router-dom'

function App() {
  return (
    <Routes>
      <Route path="/" element={<div>Главная страница</div>} />
      <Route path="/login" element={<div>Вход</div>} />
      <Route path="/register" element={<div>Регистрация</div>} />
    </Routes>
  )
}

export default App
