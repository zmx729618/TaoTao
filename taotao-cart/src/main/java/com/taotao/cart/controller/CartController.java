package com.taotao.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.cart.pojo.Cart;
import com.taotao.cart.service.CartCookieService;
import com.taotao.cart.service.CartService;
import com.taotao.cart.threadlocal.UserThreadLocal;
import com.taotao.sso.query.bean.User;

@RequestMapping("cart")
@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartCookieService cartCookieService;

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public ModelAndView cartList(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("cart");
        User user = UserThreadLocal.get();
        List<Cart> cartList = null;
        if (null == user) {
            // 未登录状态
            try {
                cartList = this.cartCookieService.queryCartlist(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 登录状态
            cartList = this.cartService.queryCartlist();
        }
        mv.addObject("cartList", cartList);
        return mv;
    }

    /**
     * 加入商品到购物车
     * 
     * @param itemId
     * @return
     */
    @RequestMapping(value = "{itemId}", method = RequestMethod.GET)
    public String addItemToCart(@PathVariable("itemId") Long itemId, HttpServletRequest request,
            HttpServletResponse response) {

        User user = UserThreadLocal.get();
        if (null == user) {
            // 未登录状态
            try {
                this.cartCookieService.addItemToCart(itemId, request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 登录状态
            this.cartService.addItemToCart(itemId);
        }

        // 重定向到购物车列表页面
        return "redirect:/cart/list.html";
    }

    @RequestMapping(value = "update/num/{itemId}/{num}", method = RequestMethod.POST)
    public ResponseEntity<Void> updateNum(@PathVariable("itemId") Long itemId,
            @PathVariable("num") Integer num, HttpServletRequest request, HttpServletResponse response) {
        User user = UserThreadLocal.get();
        if (null == user) {
            // 未登录状态
            try {
                this.cartCookieService.updateNum(itemId, num, request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 登录状态
            this.cartService.updateNum(itemId, num);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @RequestMapping(value = "delete/{itemId}", method = RequestMethod.GET)
    public String delete(@PathVariable("itemId") Long itemId, HttpServletRequest request,
            HttpServletResponse response) {
        User user = UserThreadLocal.get();
        if (null == user) {
            // 未登录状态
            try {
                this.cartCookieService.delete(itemId, request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 登录状态
            this.cartService.delete(itemId);
        }
        // 重定向到购物车列表页面
        return "redirect:/cart/list.html";
    }
}
