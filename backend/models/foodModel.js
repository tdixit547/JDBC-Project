import mongoose from "mongoose";

const foodSchema = new mongoose.Schema({
    name: {type:String, required:true},
    description:{type:String,required:true},
    price:{type:Number,required:true},
    image:{type:String,required:true},
    category:{type:String,required:true},
    rating: { type: Number, default: 0 },
    totalReviews: { type: Number, default: 0 },
    reviews: [{
        userId: String,
        rating: Number,
        comment: String,
        date: { type: Date, default: Date.now }
    }]
})

const foodModel= mongoose.models.food || mongoose.model("food", foodSchema)
export default foodModel;