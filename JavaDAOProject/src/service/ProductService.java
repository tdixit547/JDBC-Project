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

    public Product getProductById(int productId) throws SQLException, ProductNotFoundException, InvalidInputException {
        validateId(productId, "Product ID");
        Product product = productDAO.getById(productId);
        if (product == null) {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }
        return product;
    }

    public void addProduct(String name, BigDecimal price, int stock, String imageUrl) throws SQLException, InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Product name cannot be empty.");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Product price must be greater than zero.");
        }
        if (stock < 0) {
            throw new InvalidInputException("Stock count cannot be negative.");
        }
        productDAO.insert(new Product(name.trim(), price, stock, imageUrl));
    }

    public void updateProduct(Product product) throws SQLException, InvalidInputException {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new InvalidInputException("Product name cannot be empty.");
        }
        productDAO.update(product);
    }

    public void updateStock(int productId, int newCount) throws Exception {
        validateId(productId, "Product ID");
        if (newCount < 0) {
            throw new InvalidInputException("Stock count cannot be negative.");
        }
        Product existing = productDAO.getById(productId);
        if (existing == null) {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }
        productDAO.updateCount(productId, newCount);
    }

    public void deleteProduct(int productId) throws Exception {
        validateId(productId, "Product ID");
        Product existing = productDAO.getById(productId);
        if (existing == null) {
            throw new ProductNotFoundException("Product with ID " + productId + " not found.");
        }
        productDAO.delete(productId);
    }

    private void validateId(int id, String fieldName) throws InvalidInputException {
        if (id <= 0) {
            throw new InvalidInputException(fieldName + " must be a positive number.");
        }
    }
}
