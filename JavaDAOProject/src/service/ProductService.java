package service;

import dao.ProductDAO;
import factory.DAOFactory;
import model.Product;
import exceptions.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductService {

    private final ProductDAO productDAO;

    public ProductService() throws SQLException {
        this.productDAO = DAOFactory.getProductDAO();
    }

    public List<Product> getAllProducts() throws SQLException {
        return productDAO.getAll();
    }

    public Product getProductById(int productId) throws SQLException, ProductNotFoundException {
        Product product = productDAO.getById(productId);
        if (product == null) {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }
        return product;
    }

    public void addProduct(String name, BigDecimal price, int stock) throws SQLException, InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Product name cannot be empty.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Product price must be greater than zero.");
        }
        if (stock < 0) {
            throw new InvalidInputException("Stock count cannot be negative.");
        }
        productDAO.insert(new Product(name.trim(), price, stock));
    }

    public void updateProduct(Product product) throws SQLException, InvalidInputException {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new InvalidInputException("Product name cannot be empty.");
        }
        productDAO.update(product);
    }

    public void updateStock(int productId, int newCount) throws SQLException, InvalidInputException {
        if (newCount < 0) {
            throw new InvalidInputException("Stock count cannot be negative.");
        }
        productDAO.updateCount(productId, newCount);
    }

    public void deleteProduct(int productId) throws SQLException {
        productDAO.delete(productId);
    }
}
