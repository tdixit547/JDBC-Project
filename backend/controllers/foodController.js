import foodModel from "../models/foodModel.js";
import fs from 'fs'

// add food item
const addFood = async (req, res) => {
  try {
    if (!req.file) {
      return res
        .status(400)
        .json({ success: false, message: "Image is required" });
    }

    const image_filename = req.file.filename;

    const food = new foodModel({
      name: req.body.name,
      description: req.body.description,
      price: req.body.price,
      category: req.body.category,
      image: image_filename,
    });

    await food.save();

    res.json({ success: true, message: "Food Added" });
  } catch (error) {
    console.log(error);
    res.json({ success: false, message: "Error" });
  }
}

//all food list

const listFood = async(req,res)=>{
    try{
        const foods=await foodModel.find({});
        res.json({success:true,data:foods})
    }catch(error){
        console.log(error);
        res.json({success:false,message:"Error"})
    }
}


//remove food item

const removeFood = async(req,res)=>{
    try{
        const food = await foodModel.findById(req.body.id);
        fs.unlink(`uploads/${food.image}`,()=>{})
        await foodModel.findByIdAndDelete(req.body.id);
        res.json({success:true,message:"Food Removed"})
    }catch(error){
        console.log(error);
        res.json({success:false,message:"Error"})
    }
}




// add review
const addReview = async (req, res) => {
    const { foodId, userId, rating, comment } = req.body;
    try {
        const food = await foodModel.findById(foodId);
        if (!food) {
            return res.json({ success: false, message: "Food not found" });
        }

        const newReview = {
            userId,
            rating: Number(rating),
            comment,
            date: new Date()
        };

        food.reviews.push(newReview);
        food.totalReviews = food.reviews.length;

        // Recalculate average rating
        const sum = food.reviews.reduce((acc, curr) => acc + curr.rating, 0);
        food.rating = sum / food.totalReviews;

        await food.save();
        res.json({ success: true, message: "Review Added", rating: food.rating });
    } catch (error) {
        console.log(error);
        res.json({ success: false, message: "Error" });
    }
}

export { addFood, listFood, removeFood, addReview};
