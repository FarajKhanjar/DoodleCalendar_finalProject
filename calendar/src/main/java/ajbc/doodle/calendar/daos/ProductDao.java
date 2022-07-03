package ajbc.doodle.calendar.daos;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ajbc.doodle.calendar.entities.Category;
import ajbc.doodle.calendar.entities.Product;
import ajbc.doodle.calendar.entities.Supplier;


@Transactional(rollbackFor = {DaoException.class}, readOnly = true)
public interface ProductDao {

	// CRUD operations
	@Transactional(readOnly = false) //update in DB => write
	public default void addProduct(Product product) throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
	@Transactional(readOnly = false) //update in DB => write
	public default void updateProduct(Product product) throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
	@Transactional(readOnly = false) //update in DB => write
	public default void deleteProduct(Integer productId) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default Product getProduct(Integer productId) throws DaoException {
		throw new DaoException("Method not implemented");
	}


	// QUERIES
	public default List<Product> getAllProducts() throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default List<Product> getProductsByPriceRange(Double min, Double max) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default List<Product> getProductsNotInStock() throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default List<Product> getProductsOnOrder() throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default List<Product> getDiscontinuedProducts() throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
	public default Category getCategoryByProductId(Integer productId) throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
	public default Supplier getSupplierByProductId(Integer productId) throws DaoException {
		throw new DaoException("Method not implemented");
	}
	
	public default List<Product> getProductsInCategory(Integer categoryId) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	public default List<Product> getProductsOfSupplier(Integer supplierId) throws DaoException {
		throw new DaoException("Method not implemented");
	}

	
	public default long count() throws DaoException {
		throw new DaoException("Method not implemented");
	}

}
