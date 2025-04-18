package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.exceptions.APIException;
import com.ecomerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecomerce.sb_ecom.models.Category;
import com.ecomerce.sb_ecom.models.Product;
import com.ecomerce.sb_ecom.payload.ProductDTO;
import com.ecomerce.sb_ecom.payload.ProductResponse;
import com.ecomerce.sb_ecom.repositories.CategoryRepository;
import com.ecomerce.sb_ecom.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Transactional
@Service
public class ProductServiceImpl implements ProductService {


    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;

    @Value("${project.image")
    private String path; // value will be assigned from application.properties

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","CategoryId",categoryId));
        List<Product> products=category.getProducts();
        //System.out.println(products);
        for(Product product:products){
            if(product.getProductName().equals(productDTO.getProductName())){
                throw new APIException("Product already exists");
            }
        }
        Product product=modelMapper.map(productDTO,Product.class);
        product.setCategory(category);
        product.setImage("default.png");
        double specialPrice=product.getPrice() -((product.getDiscount()*0.01)*product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product SavedProduct=productRepository.save(product);
        return modelMapper.map(SavedProduct,ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sort=sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        Pageable pageable= PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> productPage=productRepository.findAll(pageable);
        System.out.println(productPage);
        List<Product>products=productPage.getContent();
        if(products.isEmpty()){
            throw new APIException("No products found");
        }
        List<ProductDTO>productDTOs=products.stream()
                .map(product->modelMapper.map(product,ProductDTO.class)).toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setPageNumber(productPage.getNumber());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setPageSize(productPage.getSize());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId,Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category=categoryRepository.findById(categoryId)
                .orElseThrow(()->new ResourceNotFoundException("Category","CategoryId",categoryId));
        Sort sort=sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        Pageable pageable=PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> productsPage=productRepository.findByCategoryOrderByPriceAsc(category,pageable);
        List<Product> products=productsPage.getContent();
        if(products.size()==0){
            throw new APIException("No products found");
        }
        List<ProductDTO> productDTOs=products.stream()
                .map(product->modelMapper.map(product,ProductDTO.class)).toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setLastPage(productsPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse getProductsByKeyword(String keyword,Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort=sortOrder.equalsIgnoreCase("asc")?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();
        Pageable pageable=PageRequest.of(pageNumber,pageSize,sort);
        Page<Product> productsPage=productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%',pageable);//% for pattern matching
        List<Product> products=productsPage.getContent();
        if(products.size()==0){
            throw new APIException("No products found");
        }
        List<ProductDTO>productDTOS=products.stream()
                .map(product->modelMapper.map(product,ProductDTO.class)).toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setPageNumber(productsPage.getNumber());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setPageSize(productsPage.getSize());
        productResponse.setLastPage(productsPage.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product product=modelMapper.map(productDTO,Product.class);
        Product existingProduct=productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","ProductId",productId));
        existingProduct.setProductName(product.getProductName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDiscount(product.getDiscount());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setSpecialPrice(product.getSpecialPrice());
        Product updatedProduct=productRepository.save(existingProduct);
        return modelMapper.map(updatedProduct,ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product=productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","ProductId",productId));
        productRepository.deleteById(productId);
        return modelMapper.map(product,ProductDTO.class);
    }

    @Override
    //throws IOException is used when you want to use try-catch block
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException { // throws IOException because you are excepting image
        Product product=productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("Product","ProductId",productId));
        if(!image.isEmpty()){
            throw new ResourceNotFoundException("Image","ProductImage",productId);
        }
        //Upload image to server
        //get the file name of uploaded image
        String fileName=fileService.uploadFile(path,image);

        //updating the new file name to the product
        product.setImage(fileName);

        //save the product into the DB
        Product updatedProduct=productRepository.save(product);
        return modelMapper.map(updatedProduct,ProductDTO.class);
    }
}
