import React, { useContext, useState } from 'react'
import './FoodItem.css'
import { assets, food_list } from '../../assets/assets'
import { StoreContext } from '../../context/StoreContext'
import FoodDetailModal from '../FoodDetailModal/FoodDetailModal'

const FoodItem = ({id,name,price,description,image, rating, totalReviews, reviews}) => {

    const {cartItems, addToCart,removeFromCart, url} = useContext(StoreContext);
    const [showModal, setShowModal] = useState(false);

    // Prioritize local images from frontend assets as per user request
    const localItem = food_list.find(item => item.name === name);
    // If local image exists, use it. Otherwise, use backend image url.
    const displayImage = localItem ? localItem.image : `${url}/images/${image}`;

    // Helper to render stars (mini version for card)
    const renderStars = (ratingValue) => {
        const stars = [];
        const r = ratingValue || 0;
        for (let i = 1; i <= 5; i++) {
            stars.push(
                <span key={i} style={{color: i <= Math.round(r) ? '#ffc107' : '#e4e5e9', fontSize: '18px'}}>★</span>
            );
        }
        return stars;
    };

    // Truncate description
    const safeDescription = description || "";
    const shortDescription = safeDescription.length > 60 ? safeDescription.substring(0, 60) + "..." : safeDescription;

    const fullItemData = {
        _id: id,
        name,
        price,
        description,
        imageUrl: displayImage,
        rating,
        totalReviews,
        reviews
    };

  return (
    <>
        <div className='food-item' onClick={() => setShowModal(true)}>
        <div className="food-item-img-container">
            <img className='food-item-image' src={displayImage} alt={name} />
            {(cartItems && !cartItems[id])
                ?<div className='add-btn' onClick={(e)=>{e.stopPropagation(); addToCart(id)}}>ADD</div>
                :<div className='food-item-counter' onClick={(e)=>e.stopPropagation()}>
                    <div onClick={()=>removeFromCart(id)} className="counter-btn">-</div>
                    <p>{cartItems[id]}</p>
                    <div onClick={()=>addToCart(id)} className="counter-btn">+</div>
                </div>
            }
        </div>
        <div className="food-item-info">
            <div className="food-item-name-rating">
                <p>{name}</p>
                <div className="rating-stars">
                    {renderStars(rating)}
                </div>
            </div>
            <p className="food-item-desc">{shortDescription}</p>
            <p className="food-item-price">${price}</p>
        </div>
        </div>
        {showModal && (
            <FoodDetailModal 
                item={fullItemData} 
                onClose={() => setShowModal(false)} 
            />
        )}
    </>
  )
}

export default FoodItem
