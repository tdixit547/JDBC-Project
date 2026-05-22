import React, { useState, useContext } from 'react'
import Navbar from './components/Navbar/Navbar'
import { Route,Routes } from 'react-router-dom'
import 'leaflet/dist/leaflet.css';
import Home from './pages/Home/Home'
import PlaceOrder from './pages/PlaceOrder/PlaceOrder'
import Cart from './pages/Cart/Cart'
import Footer from './components/Footer/Footer'
import LoginPopup from './components/LoginPopup/LoginPopup'
import Verify from './pages/Verify/Verify'
import MyOrders from './pages/MyOrders/MyOrders'

import { ThemeContext } from './context/ThemeContext';

import HelpfulBot from './components/HelpfulBot/HelpfulBot';

const App = () => {

  const [showLogin,setShowLogin] = useState(false);
  const { theme } = useContext(ThemeContext);
  
  return (
    <>
    {showLogin?<LoginPopup setShowLogin={setShowLogin}/>:<></>}
      <Navbar  setShowLogin={setShowLogin}/>
      <HelpfulBot /> 
      <div className = 'app'>
        <Routes>
          < Route path='/' element={<Home/>} />
          < Route path='/cart' element={<Cart/>} />
          < Route path='/order' element={<PlaceOrder/>} />
          < Route path='/verify' element={<Verify/>} />
          < Route path='/myorders' element={<MyOrders/>} />
        </Routes>
      </div>
      <Footer />
    </>
    
  )
}

export default App
