public class Student {

    private String id;
    private String first_name;
    private String last_name;
    private String major;
    private String currentGrade;
    private String gradeOption;
    private String honorStatus;
    private String notes;
    private String picture;

    public Student(String id, String last_name, String first_name, String major, String currentGrade, String gradeOption, String honorStatus, String notes, String picture) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.major = major;
        this.currentGrade = currentGrade;
        this.gradeOption = gradeOption;
        this.honorStatus = honorStatus;
        this.notes = notes;
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
    
    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
    
    public String getCurrentGrade() {
        return currentGrade;
    }

    public void setCurrentGrade(String currentGrade) {
        this.currentGrade = currentGrade;
    }
    
    public String getGradeOption() {
        return gradeOption;
    }

    public void setGradeOption(String gradeOption) {
        this.gradeOption = gradeOption;
    }
    
    public String getHonorStatus() {
        return honorStatus;
    }

    public void setHonorStatus(String honorStatus) {
        this.honorStatus = honorStatus;
    }
    
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

}