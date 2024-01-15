module it.emacompany.cookingwiki {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j;
    requires com.google.gson;
    requires xstream;
    requires org.mybatis;
    requires java.sql;

    opens it.emacompany.cookingwiki to javafx.fxml;
    exports it.emacompany.cookingwiki;
}
