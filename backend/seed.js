import "dotenv/config";
import mongoose from "mongoose";
import foodModel from "./models/foodModel.js";
import { connectDB } from "./config/db.js";

const generateRandomReviews = () => {
    const reviews = [];
    const numberOfReviews = Math.floor(Math.random() * 15) + 5; // 5 to 20 reviews
    let totalRating = 0;

    const dummyComments = [
        "Absolutely delicious!", "Worth every penny.", "Could be better.", "My favorite dish!",
        "Tastes authentic.", "A bit spicy for me.", "Fresh and healthy.", "Highly recommended!",
        "Will order again.", "Presentation was great."
    ];

    for (let i = 0; i < numberOfReviews; i++) {
        const rating = (Math.random() * (5 - 3) + 3).toFixed(1); // Rating between 3.0 and 5.0
        totalRating += Number(rating);
        reviews.push({
            userId: "dummy_user_id",
            rating: Number(rating),
            comment: dummyComments[Math.floor(Math.random() * dummyComments.length)],
            date: new Date(Date.now() - Math.floor(Math.random() * 10000000000))
        });
    }
    
    const averageRating = (totalRating / numberOfReviews).toFixed(1);
    return { reviews, totalReviews: numberOfReviews, rating: Number(averageRating) };
};

const food_list_base = [
    {
        name: "Greek salad",
        image: "food_1.png",
        price: 12,
        description: "A vibrant and rustic medley of sun-ripened tomatoes, crisp cucumbers, sharp red onion, briny Kalamata olives, and generous chunks of creamy feta cheese.",
        category: "Salad"
    },
    {
        name: "Veg salad",
        image: "food_2.png",
        price: 18,
        description: "Bursting with color and freshness, this wholesome vegetable salad combines a crisp array of chopped seasonal vegetables tossed in a zesty dressing.",
        category: "Salad"
    }, {
        name: "Clover Salad",
        image: "food_3.png",
        price: 16,
        description: "A delicate and nutritious blend featuring fresh clover leaves and blossoms, offering a mild, sweet flavor and a vibrant pop of color.",
        category: "Salad"
    }, {
        name: "Chicken Salad",
        image: "food_4.png",
        price: 24,
        description: "A classic and satisfying choice featuring tender, cooked chicken enveloped in a creamy dressing with crunchy celery and flavorful onion.",
        category: "Salad"
    }, {
        name: "Lasagna Rolls",
        image: "food_5.png",
        price: 14,
        description: "Experience the comforting layers of classic lasagna, reimagined into perfectly portioned rolls with savory meat sauce and decadent cheese.",
        category: "Rolls"
    }, {
        name: "Peri Peri Rolls",
        image: "food_6.png",
        price: 12,
        description: "Ignite your taste buds with these delicious rolls packed with tender chicken, marinated in a vibrant, zesty peri-peri sauce.",
        category: "Rolls"
    }, {
        name: "Chicken Rolls",
        image: "food_7.png",
        price: 20,
        description: "Savor the simple pleasure of succulent, seasoned chicken encased in a crispy exterior, offering a delightful combination of savory flavors.",
        category: "Rolls"
    }, {
        name: "Veg Rolls",
        image: "food_8.png",
        price: 15,
        description: "Refresh your palate with our vibrant Veg Rolls, bursting with an array of colorful, crisp vegetables and fresh herbs.",
        category: "Rolls"
    }, {
        name: "Ripple Ice Cream",
        image: "food_9.png",
        price: 14,
        description: "Indulge in the luscious swirls of ripple ice cream, where a rich, creamy base meets ribbons of sweet, fruity flavor.",
        category: "Deserts"
    }, {
        name: "Fruit Ice Cream",
        image: "food_10.png",
        price: 22,
        description: "Experience a burst of fresh flavor with fruit ice cream, a vibrant and creamy delight that captures the essence of ripe fruits.",
        category: "Deserts"
    }, {
        name: "Jar Ice Cream",
        image: "food_11.png",
        price: 10,
        description: "Discover the simple pleasure of jar ice cream – a delightfully creamy treat that's rich in flavor and perfect for a quick indulgence.",
        category: "Deserts"
    }, {
        name: "Vanilla Ice Cream",
        image: "food_12.png",
        price: 12,
        description: "Savor the timeless elegance of vanilla ice cream, a classic, smooth, and sweet frozen dessert perfect on its own.",
        category: "Deserts"
    },
    {
        name: "Chicken Sandwich",
        image: "food_13.png",
        price: 12,
        description: "Sink your teeth into a perfectly cooked chicken sandwich, featuring tender, juicy chicken layered with fresh toppings on a toasted bun.",
        category: "Sandwich"
    },
    {
        name: "Vegan Sandwich",
        image: "food_14.png",
        price: 18,
        description: "Discover a vibrant explosion of plant-based goodness with fresh, crisp vegetables and creamy spreads in every bite.",
        category: "Sandwich"
    }, {
        name: "Grilled Sandwich",
        image: "food_15.png",
        price: 16,
        description: "Experience the irresistible allure of a grilled sandwich, with its golden, crispy exterior giving way to warm, gooey fillings.",
        category: "Sandwich"
    }, {
        name: "Bread Sandwich",
        image: "food_16.png",
        price: 24,
        description: "Savor the simple pleasure of a bread sandwich, where freshly baked bread perfectly cradles comforting fillings.",
        category: "Sandwich"
    }, {
        name: "Cup Cake",
        image: "food_17.png",
        price: 14,
        description: "Indulge in our fluffy, moist, and delectable cupcakes, crafted to perfection with tender crumbs and rich, satisfying flavors.",
        category: "Cake"
    }, {
        name: "Vegan Cake",
        image: "food_18.png",
        price: 12,
        description: "Experience pure plant-based bliss with our vegan cakes, boasting a soft, tender, and moist crumb that's rich in flavor.",
        category: "Cake"
    }, {
        name: "Butterscotch Cake",
        image: "food_19.png",
        price: 20,
        description: "Savor the irresistible charm of our butterscotch cake, a deliciously moist creation with rich caramel toffee notes.",
        category: "Cake"
    }, {
        name: "Sliced Cake",
        image: "food_20.png",
        price: 15,
        description: "Enjoy a perfect portion of pure delight with our expertly sliced cakes, featuring tempting layers and exquisite flavors.",
        category: "Cake"
    }, {
        name: "Garlic Mushroom ",
        image: "food_21.png",
        price: 14,
        description: "Savor the aromatic embrace of tender garlic mushrooms, expertly sautéed to golden perfection.",
        category: "Pure Veg"
    }, {
        name: "Fried Cauliflower",
        image: "food_22.png",
        price: 22,
        description: "Exquisite delicate fried cauliflower florets, offering a delightful crispness that is truly satisfying.",
        category: "Pure Veg"
    }, {
        name: "Mix Veg Pulao",
        image: "food_23.png",
        price: 10,
        description: "A vibrant mixed vegetable pulao, rich with fragrant spices and wholesome grains.",
        category: "Pure Veg"
    }, {
        name: "Rice Zucchini",
        image: "food_24.png",
        price: 12,
        description: "Fluffy rice paired with tender zucchini, creating a truly satisfying and flavorful experience.",
        category: "Pure Veg"
    },
    {
        name: "Cheese Pasta",
        image: "food_25.png",
        price: 12,
        description: "Indulge in a steaming plate of pasta, glistening with a rich, velvety cheese sauce that lovingly clings to each strand.",
        category: "Pasta"
    },
    {
        name: "Tomato Pasta",
        image: "food_26.png",
        price: 18,
        description: "Savor the vibrant taste of sun-ripened tomatoes in a perfectly crafted pasta dish with aromatic herbs.",
        category: "Pasta"
    }, {
        name: "Creamy Pasta",
        image: "food_27.png",
        price: 16,
        description: "Experience the ultimate comfort with a luxurious and ultra-satisfying creamy pasta featuring a rich, velvety sauce.",
        category: "Pasta"
    }, {
        name: "Chicken Pasta",
        image: "food_28.png",
        price: 24,
        description: "Enjoy a hearty and flavorful chicken pasta, where tender pieces of chicken elevate a beloved pasta dish.",
        category: "Pasta"
    }, {
        name: "Buttter Noodles",
        image: "food_29.png",
        price: 14,
        description: "Tender, al dente noodles, perfectly coated in a rich, glistening butter sauce for a comforting experience.",
        category: "Noodles"
    }, {
        name: "Veg Noodles",
        image: "food_30.png",
        price: 12,
        description: "Savor a vibrant medley of crisp, colorful vegetables expertly stir-fried with perfectly cooked noodles.",
        category: "Noodles"
    }, {
        name: "Somen Noodles",
        image: "food_31.png",
        price: 20,
        description: "Experience the delicate elegance of somen noodles, known for their remarkably thin strands and distinctively chewy texture.",
        category: "Noodles"
    }, {
        name: "Cooked Noodles",
        image: "food_32.png",
        price: 15,
        description: "Hot, fresh, and soft yet firm noodles ready to embrace a myriad of sauces and seasonings.",
        category: "Noodles"
    }
];

const seedDB = async () => {
    try {
        await connectDB();
        await foodModel.deleteMany({});
        
        const food_list = food_list_base.map(item => {
            const reviewData = generateRandomReviews();
            return { ...item, ...reviewData };
        });

        await foodModel.insertMany(food_list);
        console.log("Database Seeded Successfully");
    } catch (error) {
        console.log(error);
    } finally {
        mongoose.connection.close();
    }
}

seedDB();
