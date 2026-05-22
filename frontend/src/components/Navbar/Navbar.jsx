import React, { useContext, useState } from 'react'
import './Navbar.css'
import {assets} from '../../assets/assets'
import { Link, useNavigate } from 'react-router-dom';
import { StoreContext } from '../../context/StoreContext'
import { ThemeContext } from '../../context/ThemeContext';

const Navbar = ({setShowLogin}) => {

    const [menu,setMenu] = useState("menu");
    const [isListening, setIsListening] = useState(false);

    const {getTotalCartAmount, token, setToken, searchTerm, setSearchTerm} = useContext(StoreContext)
    const { theme, toggleTheme } = useContext(ThemeContext);

    const navigate = useNavigate();

    const logout = () => {
        localStorage.removeItem("token");
        setToken("");
        navigate("/");
    }

    const startListening = () => {
        if ('webkitSpeechRecognition' in window) {
            const recognition = new window.webkitSpeechRecognition();
            recognition.continuous = false;
            recognition.lang = 'en-US';

            recognition.onstart = () => {
                setIsListening(true);
            };

            recognition.onresult = (event) => {
                const transcript = event.results[0][0].transcript;
                setSearchTerm(transcript);
                setIsListening(false);
            };

            recognition.onerror = (event) => {
                console.error("Speech recognition error", event);
                setIsListening(false);
            };

            recognition.onend = () => {
                setIsListening(false);
            };

            recognition.start();
        } else {
            alert("Voice search is not supported in this browser.");
        }
    };

  return (
    <div className ='navbar'>
      <Link to='/'><img src={assets.logo} alt="logo" className='logo' /></Link>
      <ul className="navbar-menu">
        <li><Link to='/' onClick={()=>setMenu("home")} className={menu==="home"?"active":""}>home</Link></li>
        <li><a href='#explore-menu' onClick={()=>setMenu("menu")} className={menu==="menu"?"active":""}>menu</a></li>
        <li><a href='#app-download' onClick={()=>setMenu("mobile-app")} className={menu==="mobile-app"?"active":""}>mobile-app</a></li>
        <li><a href='#footer' onClick={()=>setMenu("contact-us")} className={menu==="contact-us"?"active":""}>contact us</a></li>
      </ul>
      <div className="navbar-right">
         <div className="navbar-search">
            <input 
                type="text" 
                placeholder="Search..." 
                value={searchTerm} 
                onChange={(e)=>setSearchTerm(e.target.value)} 
            />
            {/* Mic Button */}
            <button 
                className={`navbar-mic ${isListening ? 'listening' : ''}`} 
                onClick={startListening}
                title="Voice Search"
            >
                {isListening ? '🔴' : '🎤'}
            </button>
            <img src={assets.search_icon} alt="" />
        </div>
        <div className="navbar-search-icon">
            <Link to='/cart'><img src={assets.basket_icon} alt="" /></Link>
            <div className={getTotalCartAmount()===0?"":"dot"}>
            </div>
        </div>
        
        <button className='theme-toggle' onClick={toggleTheme} title="Toggle Theme">
            {theme === 'light' ? '🌙' : '☀️'}
        </button>

        {!token?<button onClick={()=>setShowLogin(true)}>sign in</button>
        :<div className='navbar-profile'>
            <img src={assets.profile_icon} alt="" />
            <ul className="nav-profile-dropdown">
                <li onClick={()=>navigate('/myorders')}><img src={assets.bag_icon} alt="" /><p>Orders</p></li>
                <hr />
                <li onClick={logout}><img src={assets.logout_icon} alt="" /><p>Logout</p></li>
            </ul>
        </div>
        }
        
      </div>
    </div>
  )
}

export default Navbar
