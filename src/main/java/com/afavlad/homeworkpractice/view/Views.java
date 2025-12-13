package com.afavlad.homeworkpractice.view;

public class Views {
  public interface UserSummary{}
  public interface UserDetails extends UserSummary, OrderDetails {}

  public interface OrderSummary{}
  public interface OrderDetails extends OrderSummary {}

}
