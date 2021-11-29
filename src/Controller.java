import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.*;

import javafx.application.Platform;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

public class Controller implements Initializable {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Label subjectLabel;

    @FXML
    private TextField studentID;

    private int currentStudentID = 100001;
    
    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField major;

    @FXML
    private ChoiceBox<String> currentGrade;

    private String[] grades = {"A", "B", "C", "D", "F"};

    @FXML
    private RadioButton letterGrade;

    @FXML
    private RadioButton passNotPass;

    @FXML
    private CheckBox honorStatus;

    @FXML
    private TextArea notes;

    @FXML
    private ImageView photo;

    Image image = new Image("noImage.png");
    
    private int fileNameNo = 1;
    File saveFile = new File("filename" + fileNameNo + ".txt");

    @FXML
    private ChoiceBox<String> pic;

    private String[] pictureOptions = {"abra.png", "magmar.png", "onix.png", "growlite.png", "scyther.png"};

    @FXML
    private TableView<Student> tableView; 

    @FXML
    private TableColumn<Student, String> tableCurrentGrade;

    @FXML
    private TableColumn<Student, String> tableFirstName;

    @FXML
    private TableColumn<Student, String> tableGradeOption;

    @FXML
    private TableColumn<Student, String> tableHonorStatus;

    @FXML
    private TableColumn<Student, String> tableId;

    @FXML
    private TableColumn<Student, String> tableLastName;

    @FXML
    private TableColumn<Student, String> tableMajor;

    @FXML
    private TableColumn<Student, String> tableNotes;

    @FXML
    private TableColumn<Student, String> tablePicture;

    @FXML
    private BarChart barChart;

    @FXML
    private PieChart pieChart;

    @FXML
    private AnchorPane rosterLeftPane;

    @FXML
    private AnchorPane rosterMidPane;

    @FXML
    private AnchorPane rosterRightPane;

    @FXML
    private Label logoLabel;

    public void newStudent(ActionEvent e) {
        studentID.setText("");
        studentID.setEditable(false);
        lastName.setText("");
        firstName.setText("");
        major.setText("");
        currentGrade.valueProperty().set(null);
        letterGrade.setSelected(false);
        passNotPass.setSelected(false);
        honorStatus.setSelected(false);
        notes.setText("");
        photo.setImage(image);
        pic.valueProperty().set(null);
        updateTableView();
        loadBarChart();
        loadPieChart();
    }

    public void deleteStudent(ActionEvent e) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM roster.students WHERE id='" + studentID.getText() + "';");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        studentID.setText("");
        studentID.setEditable(false);
        lastName.setText("");
        firstName.setText("");
        major.setText("");
        currentGrade.valueProperty().set(null);
        letterGrade.setSelected(false);
        passNotPass.setSelected(false);
        honorStatus.setSelected(false);
        notes.setText("");
        photo.setImage(image);
        updateTableView();
        loadBarChart();
        loadPieChart();
    }

    public void saveChanges(ActionEvent e) {
        String grade = currentGrade.getValue();
        int gradeOption = 0;
        int honor = 0;
        String notesArea = notes.getText();

        String pImage = pic.getValue();
        Image profilePic = new Image("profilePicture/" + pImage);
        photo.setImage(profilePic);

        if (letterGrade.isSelected()) {
            gradeOption = 1;
        } else if (passNotPass.isSelected()) {
            gradeOption = 2;
        }

        if (honorStatus.isSelected()) {
            honor = 1;
        } else {
            honor = 0;
        }

        String updateID;
        
        if (studentID.getText().length() > 0) {
            updateID = studentID.getText();
            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
                Statement statement = connection.createStatement();
                statement.executeUpdate("UPDATE roster.students SET id = '" + updateID + "', last_name = '" + lastName.getText() + "', first_name = '" + firstName.getText() + "', major = '" + major.getText() + "', current_grade = '" + grade + "', grade_option = '" + gradeOption + "', honor_status = '" + honor + "', note_area = '" + notesArea + "', photo = '" + pImage + "' WHERE id = '" + updateID + "';");
                } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
                Statement statement = connection.createStatement();
                statement.executeUpdate("INSERT IGNORE INTO roster.students (id, last_name, first_name, major, current_grade, grade_option, honor_status, note_area, photo)"
                    + "VALUES   (NULL, '" + lastName.getText() + "', '" + firstName.getText() + "', '" + major.getText() + "', '" + grade + "', '" + gradeOption + "', '" + honor + "', '" + notesArea + "', '" + pImage + "');");
                
                ResultSet studentRS = statement.executeQuery("SELECT * FROM roster.students");
                while(studentRS.next()) {
                    studentID.setText(studentRS.getString("id"));
                    currentStudentID = Integer.valueOf(studentRS.getString("id"));
                }
                } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        updateTableView();
        loadBarChart();
        loadPieChart();
        studentID.setEditable(false);
    }

    public void nextStudent(ActionEvent e) {
        try {
            currentStudentID += 1;
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
            Statement statement = connection.createStatement();
            ResultSet studentRS = statement.executeQuery("SELECT * FROM roster.students WHERE id=" + currentStudentID + "");
            while (studentRS.next()) {
                studentID.setText(studentRS.getString("id"));
                firstName.setText(studentRS.getString("first_name"));
                lastName.setText(studentRS.getString("last_name"));
                major.setText(studentRS.getString("major"));
                currentGrade.valueProperty().set(studentRS.getString("current_grade"));

                if (studentRS.getString("grade_option").equals("1")) {
                    letterGrade.setSelected(true);
                } else if (studentRS.getString("grade_option").equals("2")) {
                    passNotPass.setSelected(true);
                }

                if (studentRS.getString("honor_status").equals("1")) {
                    honorStatus.setSelected(true);
                }

                notes.setText(studentRS.getString("note_area"));

                Image profilePic = new Image("profilePicture/" + studentRS.getString("photo"));
                photo.setImage(profilePic);
                pic.setValue(studentRS.getString("photo"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        updateTableView();
        loadBarChart();
        loadPieChart();
    }

    public void previousStudent(ActionEvent e) {
        try {
            currentStudentID -= 1;
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
            Statement statement = connection.createStatement();
            ResultSet studentRS = statement.executeQuery("SELECT * FROM roster.students WHERE id=" + currentStudentID + "");
            while (studentRS.next()) {
                studentID.setText(studentRS.getString("id"));
                firstName.setText(studentRS.getString("first_name"));
                lastName.setText(studentRS.getString("last_name"));
                major.setText(studentRS.getString("major"));
                currentGrade.valueProperty().set(studentRS.getString("current_grade"));

                if (studentRS.getString("grade_option").equals("1")) {
                    letterGrade.setSelected(true);
                } else if (studentRS.getString("grade_option").equals("2")) {
                    passNotPass.setSelected(true);
                }

                if (studentRS.getString("honor_status").equals("1")) {
                    honorStatus.setSelected(true);
                }

                notes.setText(studentRS.getString("note_area"));

                Image profilePic = new Image("profilePicture/" + studentRS.getString("photo"));
                photo.setImage(profilePic);
                pic.setValue(studentRS.getString("photo"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        updateTableView();
        loadBarChart();
        loadPieChart();
    }

    public void enableEdit(ActionEvent e) {
        studentID.setEditable(true);
    }

    public void newMenuItem(ActionEvent e) {
        fileNameNo += 1;
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM roster.students");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        studentID.setText("");
        studentID.setEditable(false);
        lastName.setText("");
        firstName.setText("");
        major.setText("");
        currentGrade.valueProperty().set(null);
        letterGrade.setSelected(false);
        passNotPass.setSelected(false);
        honorStatus.setSelected(false);
        notes.setText("");
        photo.setImage(image);
        pic.valueProperty().set(null);
        updateTableView();
        loadBarChart();
        loadPieChart();
    }

    public void openMenuItem(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showOpenDialog(mainPane.getScene().getWindow());

        if (file != null) {
            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
                Statement statement = connection.createStatement();
                statement.executeUpdate("DELETE FROM roster.students");
                
                Scanner myReader = new Scanner(file);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    String[] line = data.replace(" ", "").split("/");

                    statement.executeUpdate("INSERT IGNORE INTO roster.students (id, last_name, first_name, major, current_grade, grade_option, honor_status, note_area, photo)"
                    + "VALUES   ('" + line[0] + "', '" + line[2] + "', '" + line[1] + "', '" + line[3] + "', '" + line[4] + "', '" + line[5] + "', '" + line[6] + "', '" + line[7] + "', '" + line[8] + "');");

                }
                myReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        updateTableView();
        loadBarChart();
        loadPieChart();
    }

    public void saveMenuItem(ActionEvent e) {
        try {
            if (!saveFile.createNewFile()) {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
                Statement statement = connection.createStatement();
                ResultSet studentRS = statement.executeQuery("SELECT * FROM roster.students");
                FileWriter fileWriter = new FileWriter("filename" + fileNameNo + ".txt");
                while (studentRS.next()) {
                    fileWriter.write(studentRS.getString("id") + " / " + studentRS.getString("first_name") + " / " 
                                + studentRS.getString("last_name") + " / " + studentRS.getString("major") + " / " 
                                + studentRS.getString("current_grade") + " / " + studentRS.getString("grade_option") + " / "
                                + studentRS.getString("honor_status") + " / " + studentRS.getString("note_area") + " / "
                                + studentRS.getString("photo") + "\n");
                }
                fileWriter.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveAsMenuItem(ActionEvent e) {
        try {
            if (saveFile.createNewFile()) {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
                Statement statement = connection.createStatement();
                ResultSet studentRS = statement.executeQuery("SELECT * FROM roster.students");
                FileWriter fileWriter = new FileWriter("filename" + fileNameNo + ".txt");
                while (studentRS.next()) {
                    fileWriter.write(studentRS.getString("id") + " / " + studentRS.getString("first_name") + " / " 
                                + studentRS.getString("last_name") + " / " + studentRS.getString("major") + " / " 
                                + studentRS.getString("current_grade") + " / " + studentRS.getString("grade_option") + " / "
                                + studentRS.getString("honor_status") + " / " + studentRS.getString("note_area") + " / "
                                + studentRS.getString("photo") + "\n");
                }
                fileWriter.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        fileNameNo += 1;
        updateTableView();
        loadBarChart();
        loadPieChart();
    }

    public void closeMenuItem(ActionEvent e) {
        updateTableView();
        loadBarChart();
        loadPieChart();
    }

    public void exitMenuItem(ActionEvent e) {
        Platform.exit();
    }

    public void tableViewClick(MouseEvent e) {
        ObservableList<String> tcg = FXCollections.observableArrayList("A", "B", "C", "D", "F");
        ObservableList<String> tgo = FXCollections.observableArrayList("1", "2");
        ObservableList<String> ths = FXCollections.observableArrayList("0", "1");
        tableId.setCellFactory(TextFieldTableCell.forTableColumn());
        tableFirstName.setCellFactory(TextFieldTableCell.forTableColumn());
        tableLastName.setCellFactory(TextFieldTableCell.forTableColumn());
        tableMajor.setCellFactory(TextFieldTableCell.forTableColumn());
        tableCurrentGrade.setCellFactory(ChoiceBoxTableCell.forTableColumn(tcg));
        tableGradeOption.setCellFactory(ChoiceBoxTableCell.forTableColumn(tgo));
        tableHonorStatus.setCellFactory(ChoiceBoxTableCell.forTableColumn(ths));
        tableNotes.setCellFactory(TextFieldTableCell.forTableColumn());
        tablePicture.setCellFactory(TextFieldTableCell.forTableColumn());

        Student studentDetails = tableView.getSelectionModel().getSelectedItem();
        studentID.setText(studentDetails.getId());
        firstName.setText(studentDetails.getFirst_name());
        lastName.setText(studentDetails.getLast_name());
        major.setText(studentDetails.getMajor());
        currentGrade.valueProperty().set(studentDetails.getCurrentGrade());
        if (studentDetails.getGradeOption().equals("1")) {
            letterGrade.setSelected(true);
        } else if (studentDetails.getGradeOption().equals("2")) {
            passNotPass.setSelected(true);
        }
        if (studentDetails.getHonorStatus().equals("1")) {
            honorStatus.setSelected(true);
        }
        notes.setText(studentDetails.getNotes());
        Image profilePic = new Image("profilePicture/" + studentDetails.getPicture());
        photo.setImage(profilePic);
        pic.setValue(studentDetails.getPicture());

        currentStudentID = Integer.valueOf(studentDetails.getId());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentGrade.getItems().addAll(grades);
        pic.getItems().addAll(pictureOptions);

        updateTableView();
        loadBarChart();
        loadPieChart();
    }

    public List<Student> getAllstudentInfo(){
        List<Student> ll = new LinkedList<Student>();
        try{
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");;
            Statement statement = connection.createStatement();
            ResultSet studentRS = statement.executeQuery("SELECT * FROM roster.students");           

            while(studentRS.next()){   
                ll.add(new Student(
                    studentRS.getString("id"),
                    studentRS.getString("last_name"), 
                    studentRS.getString("first_name"), 
                    studentRS.getString("major"), 
                    studentRS.getString("current_grade"), 
                    studentRS.getString("grade_option"),
                    studentRS.getString("honor_status"), 
                    studentRS.getString("note_area"), 
                    studentRS.getString("photo"))); 
            }      
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return ll;
    }

    public void updateTableView() {

        tableId.setCellValueFactory(new PropertyValueFactory<Student, String>("id"));
        tableId.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student,String>>() {

            @Override
            public void handle(CellEditEvent<Student, String> event) {
                Student student = event.getRowValue();
                student.setId(event.getNewValue());
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
                    Statement statement = connection.createStatement();
                    statement.executeUpdate("UPDATE roster.students SET id = '" + event.getNewValue() + "' WHERE id = '" + studentID.getText() + "';");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tableLastName.setCellValueFactory(new PropertyValueFactory<Student, String>("last_name"));
        tableLastName.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student,String>>() {
            
            @Override
            public void handle(CellEditEvent<Student, String> event) {
                Student student = event.getRowValue();
                student.setLast_name(event.getNewValue());
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
                    Statement statement = connection.createStatement();
                    statement.executeUpdate("UPDATE roster.students SET last_name = '" + event.getNewValue() + "' WHERE id = '" + studentID.getText() + "';");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tableFirstName.setCellValueFactory(new PropertyValueFactory<Student, String>("first_name"));
        tableFirstName.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student,String>>() {
            
            @Override
            public void handle(CellEditEvent<Student, String> event) {
                Student student = event.getRowValue();
                student.setFirst_name(event.getNewValue());
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
                    Statement statement = connection.createStatement();
                    statement.executeUpdate("UPDATE roster.students SET first_name = '" + event.getNewValue() + "' WHERE id = '" + studentID.getText() + "';");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }); 

        tableMajor.setCellValueFactory(new PropertyValueFactory<Student, String>("major"));
        tableMajor.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student,String>>() {
            
            @Override
            public void handle(CellEditEvent<Student, String> event) {
                Student student = event.getRowValue();
                student.setMajor(event.getNewValue());
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
                    Statement statement = connection.createStatement();
                    statement.executeUpdate("UPDATE roster.students SET major = '" + event.getNewValue() + "' WHERE id = '" + studentID.getText() + "';");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tableCurrentGrade.setCellValueFactory(new PropertyValueFactory<Student, String>("currentGrade"));
        tableCurrentGrade.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student,String>>() {

            @Override
            public void handle(CellEditEvent<Student, String> event) {
                Student student = event.getRowValue();
                student.setCurrentGrade(event.getNewValue());
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
                    Statement statement = connection.createStatement();
                    statement.executeUpdate("UPDATE roster.students SET current_grade = '" + event.getNewValue() + "' WHERE id = '" + studentID.getText() + "';");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tableGradeOption.setCellValueFactory(new PropertyValueFactory<Student, String>("gradeOption"));
        tableGradeOption.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student,String>>() {
            
            @Override
            public void handle(CellEditEvent<Student, String> event) {
                Student student = event.getRowValue();
                student.setGradeOption(event.getNewValue());
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
                    Statement statement = connection.createStatement();
                    statement.executeUpdate("UPDATE roster.students SET grade_option = '" + event.getNewValue() + "' WHERE id = '" + studentID.getText() + "';");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tableHonorStatus.setCellValueFactory(new PropertyValueFactory<Student, String>("honorStatus"));
        tableHonorStatus.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student,String>>() {
            
            @Override
            public void handle(CellEditEvent<Student, String> event) {
                Student student = event.getRowValue();
                student.setHonorStatus(event.getNewValue());
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
                    Statement statement = connection.createStatement();
                    statement.executeUpdate("UPDATE roster.students SET honor_status = '" + event.getNewValue() + "' WHERE id = '" + studentID.getText() + "';");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tableNotes.setCellValueFactory(new PropertyValueFactory<Student, String>("notes"));
        tableNotes.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student,String>>() {
            
            @Override
            public void handle(CellEditEvent<Student, String> event) {
                Student student = event.getRowValue();
                student.setNotes(event.getNewValue());
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
                    Statement statement = connection.createStatement();
                    statement.executeUpdate("UPDATE roster.students SET note_area = '" + event.getNewValue() + "' WHERE id = '" + studentID.getText() + "';");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tablePicture.setCellValueFactory(new PropertyValueFactory<Student, String>("picture"));
        tablePicture.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Student,String>>() {
            
            @Override
            public void handle(CellEditEvent<Student, String> event) {
                Student student = event.getRowValue();
                student.setPicture(event.getNewValue());
                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
                    Statement statement = connection.createStatement();
                    statement.executeUpdate("UPDATE roster.students SET photo = '" + event.getNewValue() + "' WHERE id = '" + studentID.getText() + "';");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        tableView.getItems().setAll(getAllstudentInfo());
    }

    public void loadBarChart() {

        try{
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");;
            Statement statement = connection.createStatement();
            ResultSet barChartRS = statement.executeQuery("SELECT current_grade, COUNT(*) as grade_count FROM roster.students GROUP BY current_grade ORDER BY current_grade;");
            XYChart.Series<String, Integer> barChartData = new XYChart.Series<>();
            barChartData.getData().removeAll(Collections.singleton(barChart.getData().setAll()));
            while(barChartRS.next()){ 
                barChartData.getData().add(new XYChart.Data<>(barChartRS.getString("current_grade"), Integer.valueOf(barChartRS.getString("grade_count"))));
            }
            barChart.setTitle("Letter Grade Frequencies");
            barChart.getData().addAll(barChartData);
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void loadPieChart() {
        try{
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");;
            Statement statement = connection.createStatement();
            ResultSet pieChartRS = statement.executeQuery("SELECT major, COUNT(*) as major_count, ROUND(COUNT(*) / (SELECT COUNT(*) FROM roster.students) * 100) AS percentage FROM roster.students GROUP BY major ORDER BY major_count DESC;");           
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            while(pieChartRS.next()){   
                pieChartData.add(new PieChart.Data(pieChartRS.getString("major") + ": " + pieChartRS.getString("major_count") + " Stds - "  + pieChartRS.getString("percentage") + "%", Integer.valueOf(pieChartRS.getString("major_count"))));
            }    
            
            pieChart.setTitle("% Of Students in Different Majors");
            pieChart.setData(pieChartData);
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }
}