import io.cucumber.java.bm.Tetapi;
import lombok.extern.java.Log;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.awt.*;
import java.awt.Robot;
import java.awt.event.KeyEvent;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DhrAllPageTest extends BaseStep {


    @Test
    @Order(1)
    @DisplayName("Tarayıcıyı Aç ")
    public void OpenDriver(){
        LogTest.info("Tarayıcı açılıyor");
        BaseStep.openChromeDriver();
        LogTest.info("Tarayıcı açıldır");
        LogTest.info("Kullanıcı adı Input aranıyor");
        WebElement usernameInput =BaseStep.findElementXpathWithWait("//*[@id=\"login_email\"]",TimeOut.SHORT.value);
        BaseStep.clearAndType(usernameInput,"admin@d1-tech.com","Kullanıcı Adı");
        LogTest.info("Kullanıcı adı gönderildi");
        WebElement passwordInput=BaseStep.findElementXpathWithWait("//*[@id=\"login_password\"]",TimeOut.SHORT.value);
        LogTest.info("Parola Inputu bulunuyor");
        BaseStep.clearAndType(passwordInput,"Admin123!","Şifre");
        LogTest.info("Parola gönderildi");
        LogTest.info("Giriş Yap butonu bulunuyor");
        WebElement loginClickButton= BaseStep.findElementXpathWithWait("(//button[@type='submit'])[1]",TimeOut.SHORT.value);
        BaseStep.clickElement(loginClickButton,"Giriş yap butonuna tıklandı");

    }

    @Test
    @Order(2)
    @DisplayName("Dashboard sayfası açılıyor")
    public void dashboardPageTotalEmployeeCard(){
        LogTest.info("Dashboard sayfası testlerine başlanıyor.");
        LogTest.info("Toplam Personel kartının elementi bulunuyor.");
        WebElement buttonManagment=BaseStep.findElementXpathWithWait("//*[@id=\"root\"]/section/section/main/div/div[2]/div[1]/div/div",TimeOut.SHORT.value);
        LogTest.info("Toplam Personeller kartının elementi bulundu");
        BaseStep.clickElement(buttonManagment,"İzin Yönetimi butonuna tıklandı");
        LogTest.info("Toplam Personeller kartının elementine tıklandı");
        BaseStep.waitSeconds(2);
        LogTest.info("Dashboard sayfasına gitmek için Url metoduna tıklandı.");
        BaseStep.navigateToUrl("https://dhrtest.d1-tech.com.tr/dashboard");
        BaseStep.waitSeconds(2);
        LogTest.info("Dashboard sayfasına geri dönüldü");
    }

    @Test
    @Order(3)
    @DisplayName("Pasif Personeller Kartı Test Ediliyor")
    public void dashboardPagePassiveEmployeeCard(){
        LogTest.info("Pasif personeller kartı elementi aranıyor");
        BaseStep.waitSeconds(2);
        WebElement passiveEmployeCard = BaseStep.findElementXpathWithWait("//*[@id=\"root\"]/section/section/main/div/div[2]/div[2]/div",TimeOut.SHORT.value);
       // BaseStep.waitSeconds(2);
        LogTest.info("Pasif personeller kartına tıklanıyor");
        BaseStep.clickElement(passiveEmployeCard,"pasif personeller kartına tıklandı");
        BaseStep.waitSeconds(2);
        LogTest.info("Pasif personeller kartına tıklandı");
        LogTest.info("Pasif Personeller Popup Açıldı");
        WebElement passiveEmployeePopup= BaseStep.findElementXpathWithWait("/html/body/div[3]/div/div[2]/div/div[2]",TimeOut.SHORT.value);
        LogTest.info("Pasif Personeller Popup elementi bulundu");
        WebElement passiveEmployeePopupCloseButton= passiveEmployeePopup.findElement(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/button"));
        LogTest.info("Pasif personeller popup kapat butonu bulundu");
        BaseStep.clickElement(passiveEmployeePopupCloseButton,"Pasif personeller popup kapatıldı");
        LogTest.info("Pasif Personeller Popup kapatıldı");
        BaseStep.waitSeconds(2);
    }

    @Test
    @Order(4)
    @DisplayName("Departman Kartı Test Ediliyor")
    public void dashboardPageDepartmentCard() throws AWTException {
        LogTest.info("Dashboard sayfasına dönüldü");
        LogTest.info("Departman kartının elementi bulunuyor");
        WebElement departmentButton= BaseStep.findElementXpathWithWait("//*[@id=\"root\"]/section/section/main/div/div[2]/div[3]/div/div",TimeOut.SHORT.value);
        LogTest.info("Departman kartının elementi bulundı");
        BaseStep.clickElement(departmentButton,"");
        LogTest.info("Departman kartına tıklandı");


      //  WebElement departmentModal=BaseStep.findElementClassNameWithWait("ant-modal-content",TimeOut.SHORT.value);

        LogTest.info("Departman kartının kapat buton elementi bulundu");
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ESCAPE);
        robot.keyRelease(KeyEvent.VK_ESCAPE);
      //  WebElement departmentCloseModalButton = departmentModal.findElement(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/button/span"));
      //  BaseStep.clickElement(departmentCloseModalButton,"modal kapandı");
        BaseStep.waitSeconds(2);
        LogTest.info("Departman kartının kapat butonuna tıklandı");
    }

    @Test
    @Order(5)
    @DisplayName("Boş Personeller Kartı Test Ediliyor")
    public void  dashboardPageVarcantEmployeeCard(){

        LogTest.info("Boş Personeller kart elementi bulunuyor");
        WebElement vacantEmployeeCard= BaseStep.findElementXpathWithWait("//*[@id=\"root\"]/section/section/main/div/div[2]/div[4]/div",TimeOut.SHORT.value);
        BaseStep.clickElement(vacantEmployeeCard,"");
        LogTest.info("Boş Personeller kartına tıklandı");
        BaseStep.waitSeconds(2);
        WebElement buttonCloseModalVacantEmployee= vacantEmployeeCard.findElement(By.xpath("/html/body/div[3]/div/div[2]/div/div[2]/button"));
        BaseStep.clickElement(buttonCloseModalVacantEmployee,"Modal kapatma butonuna tıklandı");
        BaseStep.waitSeconds(2);


      //  BaseStep.clickElement(buttonCloseDepartmentModal,"Departman kartının kapat buton elementi t");
    }

    @Test
    @Order(6)
    @DisplayName("Employee Managment Sayfası")
    public void employeeManagementPage(){

    }

    @Test
    @Order(7)
    @DisplayName("Document Management Page")
    public void documentManagementPage(){

    }

    @Test
    @Order(8)
    @DisplayName("Leave Management Page")
    public void leaveManagementPage(){

    }





}
