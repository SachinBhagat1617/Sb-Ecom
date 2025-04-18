package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.exceptions.APIException;
import com.ecomerce.sb_ecom.exceptions.ResourceNotFoundException;
import com.ecomerce.sb_ecom.models.Cart;
import com.ecomerce.sb_ecom.models.CartItem;
import com.ecomerce.sb_ecom.models.Product;
import com.ecomerce.sb_ecom.payload.CartDTO;
import com.ecomerce.sb_ecom.payload.ProductDTO;
import com.ecomerce.sb_ecom.repositories.CartItemRepository;
import com.ecomerce.sb_ecom.repositories.CartRepository;
import com.ecomerce.sb_ecom.repositories.ProductRepository;
import com.ecomerce.sb_ecom.utils.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional
public class CartServiceImpl implements CartService {
    @Autowired
    AuthUtil authUtil;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    private ModelMapper modelMapper;


    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        Cart cart=createCart();
        Product product=productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("Product","ProductId",productId));
        CartItem cartItem=cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(),productId);
        if(cartItem!=null){
            throw new APIException("product"+product.getProductId()+"already exists");
        }
        if(product.getQuantity()==0){
            throw new APIException("product"+product.getProductId()+"quantity is 0");
        }
        if(product.getQuantity()<quantity){
            throw new APIException("product"+product.getProductId()+"not enough");
        }
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProduct_price(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);
        cart.getCartItems().add(newCartItem);
        //product.setQuantity(product.getQuantity()-quantity); // we will update quantity when payment is successfull
        product.setQuantity(product.getQuantity());
        cart.setTotal_price(cart.getTotal_price()+(product.getSpecialPrice()*quantity));
        cartRepository.save(cart);

        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class); // cart ko CartDTO mai convert karo
        List<CartItem> cartItems=cart.getCartItems();
       //System.out.println("Cart items: " + newCartItem);
        Stream<ProductDTO> productDTO=cartItems.stream()
                .map(item->{
                    ProductDTO map=modelMapper.map(item.getProduct(),ProductDTO.class);
                    System.out.println(item.getProduct());
                    map.setQuantity(item.getQuantity());
                    return map;
                });
        cartDTO.setProducts(productDTO.toList());
        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts=cartRepository.findAll();
        if(carts.size()==0){
            throw new APIException("No carts found");
        }
        List<CartDTO> cartDTOs=carts.stream().map(cart->{
            CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
            List<ProductDTO> products=cart.getCartItems().stream()
                    .map(item->modelMapper.map(item.getProduct(),ProductDTO.class)).toList();
            cartDTO.setProducts(products);
            return cartDTO;
        }).toList();
        return cartDTOs;
    }

    @Override
    public CartDTO getUserCart() {
        String email= authUtil.loggedInEmail();
        Cart cart=cartRepository.findCartByEmail(email);
        CartDTO cartDTO=modelMapper.map(cart,CartDTO.class);
        List<ProductDTO> productDTOList=cart.getCartItems().stream().map(item->{
            ProductDTO productDTO=modelMapper.map(item.getProduct(),ProductDTO.class);
            productDTO.setQuantity(item.getQuantity());
            return productDTO;
        }).toList();
        cartDTO.setProducts(productDTOList);
        return cartDTO;
    }

    @Override
    public CartDTO updateProductQuantityInCart(Long productId, int operations) {

        return null;
    }

    private Cart createCart() {
        Cart userCart=cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart!=null){
            return userCart;
        }
        Cart cart=new Cart();
        cart.setUser(authUtil.loggedInUser());
        cart.setTotal_price(0.0);
        Cart newCart=cartRepository.save(cart);
        return newCart;
    }
}
