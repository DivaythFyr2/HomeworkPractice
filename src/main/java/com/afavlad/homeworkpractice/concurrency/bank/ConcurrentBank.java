package com.afavlad.homeworkpractice.concurrency.bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentBank {

  private final AtomicLong nextId = new AtomicLong(1);

  private final List<BankAccount> accounts =
      Collections.synchronizedList(new ArrayList<>());

  /**
   * Создаёт новый счёт с заданным начальным балансом и регистрирует его в банке.
   *
   * @param initialBalance начальный баланс счёта
   * @return созданный BankAccount
   */
  public BankAccount createAccount(long initialBalance) {
    long id = nextId.getAndIncrement();
    BankAccount account = new BankAccount(id, initialBalance);
    accounts.add(account);
    return account;
  }

  /**
   * Перевод средств между двумя счетами. Операция атомарна: либо деньги успешно списаны с одного
   * счёта и зачислены на другой, либо перевод не выполняется (например, при нехватке средств).
   * <p>
   * Для предотвращения дедлоков при одновременных переводах между несколькими счетами всегда
   * захватываем блокировки в фиксированном порядке: сначала счёт с меньшим id, затем с большим.
   *
   * @param from   счёт-источник
   * @param to     счёт-получатель
   * @param amount сумма перевода
   */
  public void transfer(BankAccount from, BankAccount to, long amount) {
    if (from == null || to == null) {
      throw new IllegalArgumentException("Accounts must not be null");
    }
    if (amount <= 0) {
      throw new IllegalArgumentException("Transfer amount must be > 0");
    }
    if (from == to) {
      return;
    }

    BankAccount firstLock;
    BankAccount secondLock;

    if (from.getId() < to.getId()) {
      firstLock = from;
      secondLock = to;
    } else {
      firstLock = to;
      secondLock = from;
    }

    synchronized (firstLock) {
      synchronized (secondLock) {
        if (!from.withdraw(amount)) {
          System.out.println("Transfer failed: insufficient funds. From=" + from);
          return;
        }

        to.deposit(amount);
        System.out.println("Transfer succeeded: " + amount + " from " + from.getId() + " to " + to.getId());
      }
    }
  }

  /**
   * Возвращает общий баланс всех счетов в банке.
   * <p>
   * Важно: в данном примере метод не даёт строго "мгновенный" снимок состояния при параллельных
   * переводах, но: - каждый отдельный баланс читается потокобезопасно, если переводы закончены -
   * сумма будет корректной.
   *
   * @return сумма балансов всех счетов
   */
  public long getTotalBalance() {
    long sum = 0L;
    synchronized (accounts) {
      for (BankAccount account : accounts) {
        sum += account.getBalance();
      }
    }
    return sum;
  }
}