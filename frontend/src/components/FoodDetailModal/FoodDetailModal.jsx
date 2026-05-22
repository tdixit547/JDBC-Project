import React, { useState, useContext } from 'react';
import './FoodDetailModal.css';
import { StoreContext } from '../../context/StoreContext';
import axios from 'axios';

const FoodDetailModal = ({ item, onClose }) => {
    const { url } = useContext(StoreContext);
    const [rating, setRating] = useState(5);
    const [comment, setComment] = useState("");
    const [reviews, setReviews] = useState(item.reviews || []);
    const [itemRating, setItemRating] = useState(item.rating || 0);
    const [totalReviews, setTotalReviews] = useState(item.totalReviews || 0);

    // Helper to render stars
    const renderStars = (ratingValue, interactive = false, setRatingFunc = null) => {
        const stars = [];
        for (let i = 1; i <= 5; i++) {
            const isFilled = i <= Math.round(ratingValue);
            stars.push(
                <span 
                    key={i} 
                    className={`star-icon ${isFilled ? 'filled' : ''} ${interactive && i <= rating ? 'active' : ''}`}
                    onClick={() => interactive && setRatingFunc(i)}
                >
                    ★
                </span>
            );
        }
        return stars;
    };

    const handleSubmitReview = async () => {
        if (!comment.trim()) {
            alert("Please enter a comment");
            return;
        }

        try {
            const response = await axios.post(`${url}/api/food/review`, {
                foodId: item._id,
                userId: "user_guest", // Generic user for now
                rating: rating,
                comment: comment
            });

            if (response.data.success) {
                // Optimistically update UI or fetch specific item (simplifying by updating local state)
                const newReview = {
                    userId: "You",
                    rating: rating,
                    comment: comment,
                    date: new Date()
                };
                setReviews([newReview, ...reviews]);
                setTotalReviews(prev => prev + 1);
                setItemRating(response.data.rating); // Use new rating from backend
                setComment("");
                alert("Review submitted!");
            } else {
                alert("Failed to submit review");
            }
        } catch (error) {
            console.error(error);
            alert("Error submitting review");
        }
    };

    if (!item) return null;

    return (
        <div className="food-detail-modal-overlay" onClick={onClose}>
            <div className="food-detail-modal-content" onClick={e => e.stopPropagation()}>
                <button className="food-detail-close-btn" onClick={onClose}>×</button>
                
                <div className="food-detail-header">
                    <img className="food-detail-image" src={item.imageUrl} alt={item.name} />
                </div>

                <div className="food-detail-body">
                    <div className="food-detail-info">
                        <div className="food-detail-name-rating">
                            <h2>{item.name}</h2>
                            <div className="food-detail-stars">
                                {renderStars(itemRating)} ({totalReviews})
                            </div>
                        </div>
                        <p className="food-detail-price">${item.price}</p>
                        <p className="food-detail-desc">{item.description}</p>
                    </div>

                    <div className="food-reviews-section">
                        <h3>Reviews</h3>
                        
                        <div className="reviews-list">
                            {reviews.length > 0 ? reviews.map((rev, index) => (
                                <div key={index} className="review-item">
                                    <div className="review-header">
                                        <span className="review-user">{rev.userId === "user_guest" ? "Guest" : rev.userId}</span>
                                        <span className="review-date">{new Date(rev.date).toLocaleDateString()}</span>
                                    </div>
                                    <div className="food-detail-stars" style={{fontSize: '14px', marginBottom: '5px'}}>
                                        {renderStars(rev.rating)}
                                    </div>
                                    <p className="review-comment">{rev.comment}</p>
                                </div>
                            )) : <p>No reviews yet.</p>}
                        </div>

                        <div className="add-review-form">
                            <h4>Write a Review</h4>
                            <div className="rating-input">
                                {renderStars(rating, true, setRating)}
                            </div>
                            <textarea 
                                className="review-textarea" 
                                placeholder="Share your thoughts..."
                                value={comment}
                                onChange={e => setComment(e.target.value)}
                            ></textarea>
                            <button className="submit-review-btn" onClick={handleSubmitReview}>Submit Review</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default FoodDetailModal;
