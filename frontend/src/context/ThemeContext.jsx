import React, { createContext, useState, useEffect } from 'react';

export const ThemeContext = createContext();

const ThemeContextProvider = (props) => {
    const [theme, setTheme] = useState("light");

    useEffect(() => {
        const storedTheme = localStorage.getItem("theme");
        if (storedTheme) {
            setTheme(storedTheme);
            document.body.dataset.theme = storedTheme;
        } else {
            document.body.dataset.theme = "light";
        }
    }, []);

    const toggleTheme = () => {
        setTheme((prev) => {
            const newTheme = prev === "light" ? "dark" : "light";
            localStorage.setItem("theme", newTheme);
            document.body.dataset.theme = newTheme;
            return newTheme;
        });
    };

    return (
        <ThemeContext.Provider value={{ theme, toggleTheme }}>
            {props.children}
        </ThemeContext.Provider>
    );
};

export default ThemeContextProvider;
