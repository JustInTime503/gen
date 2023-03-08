import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;
import java.util.Arrays;


public class main {

    private static final String URL =
"https://pornpen.ai/make?tags=OYUwdiBOCGAuD2kC8B3eBbcB9AxpAlgM4AOAZLNMIUhVVgDbxjBYAW0+kANLYVsVABmIHLCwAjePHGEu4AG4SQ0HKy6UQWAIwAOLuOiFNhRInywAnutH4mWMcXhGelPilbmQLuoXTR69FiGsrz8QiJikgAmVqHiygCusPiCCfTefIQA1vhgYLGuEpAJELCwXvL4ICgSKllAA";
    private static final int fensterZahl = 20;
    private static final String mail = "prnpn@gmail.com";
    private static final String pw = "styler503";
    private static final int secWartenZwischenVersuchen = 10;
    private static final String xPathAccept = "/html/body/div[2]/div/div/div/div[3]/button[1]/span";
    private static final String xPathSignIn = "//*[@id=\"root\"]/div/div[1]/div[2]/div[5]/a";
    private static final String xPathMailAndPw = "//*[@id=\"root\"]/div/div[2]/div[2]/div[2]/div[4]/button";
    private static final String xPathMailField = "//*[@id=\"email2\"]";
    private static final String xPathPwField = "//*[@id=\"password\"]";
    private static final String xPathSignIn2 = "//*[@id=\"root\"]/div/div[2]/div[2]/div[3]/div[4]/button[2]";
    private static final String xPathGen = "//*[@id=\"root\"]/div/div[2]/div[1]/div[2]/button";
    private static final String xPathSaveWhenDone = "//*[@id=\"root\"]/div/div[2]/div[2]/div/div[2]/button";
    private static final String xPathFailed = "//*[@id=\"root\"]/div/div[2]/div[1]/div[4]/a";

    public static void main(String[] args) {
        generatePics();
    }

    private static void generatePics() {
        System.setProperty("webdriver.chrome.driver",
                "D:\\Programmieren\\Java\\CromeDriver\\chromedriver.exe");

        ArrayList<WebDriver> drivers = new ArrayList<>();
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability("chrome.switches", Arrays.asList("--incognito"));

        for (int i = 0; i < fensterZahl; i++) {
            WebDriver driver = new ChromeDriver(capabilities);
            drivers.add(driver);
            driver.get(URL);
            WebElement accept = driver.findElement(By.xpath(xPathAccept));
            accept.click();
            WebElement signIn = driver.findElement(By.xpath(xPathSignIn));
            signIn.click();
            WebElement mailAndPw = driver.findElement(By.xpath(xPathMailAndPw));
            mailAndPw.click();
            WebElement mailField = driver.findElement(By.xpath(xPathMailField));
            mailField.sendKeys(mail);
            WebElement pwField = driver.findElement(By.xpath(xPathPwField));
            pwField.sendKeys(pw);
            WebElement signIn2 = driver.findElement(By.xpath(xPathSignIn2));
            signIn2.click();
            sleep(1);
            driver.get(URL);
        }
        sleep(1);

        while (true) {
            for (WebDriver driver : drivers) {
                handleDriver(driver);
            }
            sleep(secWartenZwischenVersuchen);
            killBadProcesses();
        }
    }

    private static void killBadProcesses() {
        System.out.println("Killing Bad Processes");
        ProcessHandle.allProcesses().forEach(processHandle -> {
            if (isCromeProcess(processHandle)) {
                killBadSubprocesses(processHandle);
            }
        });
    }

    private static boolean isCromeProcess(ProcessHandle processHandle) {
        return processHandle.info().toString().contains("chrome.exe");
    }

    private static void killBadSubprocesses(ProcessHandle processHandle) {
        processHandle.children().forEach(process -> {
            if (process.info().toString().contains("software_reporter_tool.exe")) {
                System.out.println("Killing Bad Process: \n" + process.info() + "\n\n");
                process.destroy();
            }
        });
    }

    private static void handleDriver(WebDriver driver) {
        Status status = getStatus(driver);
        switch (status) {
            case FAILED -> {
                gen(driver);
            }
            case DONE -> {
                save(driver);
                sleep(1);
                driver.get(URL);
                gen(driver);
            }
            case INIT -> {
                gen(driver);
            }
        }
    }

    private static void gen(WebDriver driver) {
        WebElement generate = driver.findElement(By.xpath(xPathGen));
        generate.click();
    }


    private static void save(WebDriver driver) {
        System.out.println("Speichere Bild");
        WebElement save = driver.findElement(By.xpath(xPathSaveWhenDone));
        save.click();
    }

    private static void sleep(long secs) {
        try {
            Thread.sleep(secs * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Status getStatus(WebDriver driver) {
        try {
            driver.findElement(By.xpath(xPathFailed));
            return Status.FAILED;
        } catch (Exception e) {
            try {
                driver.findElement(By.xpath(xPathSaveWhenDone));
                return Status.DONE;
            } catch (Exception ex) {
                return Status.INIT;
            }

        }

    }
}
