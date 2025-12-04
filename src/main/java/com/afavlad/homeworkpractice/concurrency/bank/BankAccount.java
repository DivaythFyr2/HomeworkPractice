package com.afavlad.homeworkpractice.concurrency.bank;

public class BankAccount {

  private final long id;
  private long balance;

  public BankAccount(long id, long initialBalance) {
    if (initialBalance < 0) {
      throw new IllegalArgumentException("Initial balance must be >= 0");
    }
    this.id = id;
    this.balance = initialBalance;
  }

  /**
   * Уникальный идентификатор счёта.
   */
  public long getId() {
    return id;
  }

  /**
   * Потокобезопасное пополнение счёта.
   *
   * @param amount сумма пополнения, должна быть > 0
   */
  public synchronized void deposit(long amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Deposit amount must be > 0");
    }
    balance += amount;
  }

  /**
   * Потокобезопасное снятие средств.
   *
   * @param amount сумма снятия, должна быть > 0
   * @return true, если средств хватило и операция прошла успешно;
   *         false, если средств недостаточно (баланс не изменён).
   */
  public synchronized boolean withdraw(long amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Withdraw amount must be > 0");
    }
    if (balance >= amount) {
      balance -= amount;
      return true;
    }
    return false;
  }

  /**
   * Потокобезопасное чтение баланса.
   */
  public synchronized long getBalance() {
    return balance;
  }

  @Override
  public String toString() {
    return "BankAccount{id=" + id + ", balance=" + balance + '}';
  }
}
