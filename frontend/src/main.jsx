import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.jsx'
import { BrowserRouter } from 'react-router-dom'
import './index.css'
import StoreContextProvider from './context/StoreContext.jsx'
import ThemeContextProvider from './context/ThemeContext.jsx'
createRoot(document.getElementById('root')).render(
  <BrowserRouter>
    <StoreContextProvider>
     <ThemeContextProvider>
      <App />
     </ThemeContextProvider>
    </StoreContextProvider>
  </BrowserRouter>
);
