Feature: Kullanıcı Giriş İşlemleri
  Bir kullanıcı olarak
  Sisteme güvenli bir şekilde giriş yapmak istiyorum
  Böylece hesabıma erişebilirim

  Background:
    Given kullanıcı ana sayfada
    When giriş yap butonuna tıklar

  @smoke @regression
  Scenario: Geçerli bilgilerle başarılı giriş
    Given kullanıcı giriş sayfasında
    When kullanıcı adı alanına "test@example.com" yazar
    And şifre alanına "123456" yazar
    And giriş yap butonuna tıklar
    Then ana sayfa yüklenmeli
    And kullanıcı adı "Test Kullanıcı" olarak görünmeli

  @negative
  Scenario: Geçersiz şifre ile giriş denemesi
    Given kullanıcı giriş sayfasında
    When kullanıcı adı alanına "test@example.com" yazar
    And şifre alanına "yanlisşifre" yazar
    And giriş yap butonuna tıklar
    Then hata mesajı "Geçersiz kullanıcı adı veya şifre" görünmeli
    And kullanıcı giriş sayfasında kalmalı

  @negative
  Scenario: Boş alanlarla giriş denemesi
    Given kullanıcı giriş sayfasında
    When giriş yap butonuna tıklar
    Then kullanıcı adı için "Bu alan zorunludur" hatası görünmeli
    And şifre için "Bu alan zorunludur" hatası görünmeli

  Scenario Outline: Farklı geçersiz email formatlarıyla giriş
    Given kullanıcı giriş sayfasında
    When kullanıcı adı alanına "<email>" yazar
    And şifre alanına "123456" yazar
    And giriş yap butonuna tıklar
    Then email format hatası "<hata_mesajı>" görünmeli

    Examples:
      | email          | hata_mesajı                    |
      | test           | Geçerli bir email giriniz      |
      | test@          | Geçerli bir email giriniz      |
      | @example.com   | Geçerli bir email giriniz      |
      | test.example   | Geçerli bir email giriniz      |

  @security
  Scenario: Çok fazla başarısız giriş denemesi
    Given kullanıcı giriş sayfasında
    When kullanıcı 5 kez yanlış şifre ile giriş dener
    Then hesap geçici olarak kilitlenmeli
    And "Hesabınız güvenlik nedeniyle kilitlendi" mesajı görünmeli