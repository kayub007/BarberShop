package com.example.barbershop.Database;

import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.util.Log;

import com.example.barbershop.Common.Common;
import com.example.barbershop.Interface.ICountItemInCartListener;

import java.util.List;

public class DatabaseUtils {
    //Because all room handles need work on other thread

    public static void getAllItemFromCart(CartDatabase db)
    {
        GetAllCartAsync task = new  GetAllCartAsync(db);
        task.execute(Common.currentUser.getPhoneNumber());
    }

    public static void insertToCart(CartDatabase db,CartItem...cartItems)
    {
        InsetToCartAsync task = new  InsetToCartAsync(db);
        task.execute(cartItems);
    }

    public static void countItemInCart(CartDatabase db, ICountItemInCartListener iCountItemInCartListener)
    {
        CountItemInCartAsync task = new  CountItemInCartAsync(db, iCountItemInCartListener);
        task.execute();
    }

    /*
    ========================================================
    ASYNC TASK DEFINE
    ========================================================
    */

    private static class GetAllCartAsync extends AsyncTask<String,Void,Void>{

        CartDatabase db;
        public GetAllCartAsync(CartDatabase cartDatabase) {
            db = cartDatabase;
        }

        @Override
        protected Void doInBackground(String... strings) {
            getAllItemFromCartByUserPhone(db,strings[0]);
            return null;
        }

        private void getAllItemFromCartByUserPhone(CartDatabase db, String userPhone) {
           List<CartItem> cartItems = db.cartDAO().getAllItemFromCart(userPhone);
            Log.d("COUNT_CART",""+cartItems.size());
        }
    }

    private static class InsetToCartAsync extends AsyncTask<CartItem,Void,Void>{

        CartDatabase db;
        public InsetToCartAsync(CartDatabase cartDatabase) {
            db = cartDatabase;
        }

        @Override
        protected Void doInBackground(CartItem... cartItems) {
            insertToCart(db,cartItems[0]);
            return null;
        }

        private void insertToCart(CartDatabase db, CartItem cartItem) {
            //If item already available in the cart, just increase quantity
            try {
                db.cartDAO().insert(cartItem);
            }catch (SQLiteConstraintException exception)
            {
                CartItem updateCartItem = db.cartDAO().getProductInCart(cartItem.getProductId(),
                        Common.currentUser.getPhoneNumber());
                updateCartItem.setProductQuantity(updateCartItem.getProductQuantity()+1);
                db.cartDAO().update(updateCartItem);
            }
        }
    }

    private static class CountItemInCartAsync extends AsyncTask<Void,Void,Integer>{

        CartDatabase db;
        ICountItemInCartListener listener;
        public CountItemInCartAsync(CartDatabase cartDatabase,ICountItemInCartListener iCountItemInCartListener) {
            db = cartDatabase;
            listener = iCountItemInCartListener;
        }


        @Override
        protected Integer doInBackground(Void... voids) {
            countItemInCartRun(db);
            return Integer.parseInt(String.valueOf(countItemInCartRun(db)));
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            listener.onCartItemCountSuccess(integer.intValue());
        }

        private int countItemInCartRun(CartDatabase db) {
            return db.cartDAO().countItemInCart(Common.currentUser.getPhoneNumber());
        }
    }
}
