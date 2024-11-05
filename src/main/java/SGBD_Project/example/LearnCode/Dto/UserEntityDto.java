package SGBD_Project.example.LearnCode.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntityDto {
    private String id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private LocalDate birthDay;
    private String address;
    private Boolean isBanned;
    private LocalDateTime createdDate;
    private LocalDateTime  updatedDate;
    private Set<String> questionsId;
    private Set<Integer> topicsId;
    private Set<String> topicsName;


    public static boolean isValidTopics(Set<Integer> topics) {
        if (topics == null || topics.isEmpty() || topics.size()<3) {
            return false;
        }
        for(int topic : topics) {
            if(topic<0 ) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidName(String name) {
        return name.length() >= 2 && name.length() <= 50;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return  pattern.matcher(email).matches();
    }

    public static boolean isValidBirthDay(LocalDate birthDay) {
        return birthDay.isBefore(LocalDate.now());
    }


    public static boolean isValidAddress(String address) {
        return  address.length() >= 5 && address.length() <= 100;
    }
    public  boolean checkError(){
        return email!=null && !email.isEmpty()
                && password!=null && !password.isEmpty()
                && birthDay!=null
                && address!=null && !address.isEmpty()
                && name!=null && !name.isEmpty()
                && lastName!=null && !lastName.isEmpty();
    }

    public Map<String, Object> userMap() {
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", this.id);
        dto.put("name", this.name);
        dto.put("lastName", this.lastName);
        dto.put("email", this.email);
        dto.put("birthDay", this.birthDay);
        dto.put("address", this.address);
        dto.put("isBanned", this.isBanned);
        dto.put("createdDate", this.createdDate);
        dto.put("updatedDate", this.updatedDate);
        return dto;
    }



  /*  public static boolean isValidUser(String name, String lastName, String email, LocalDate birthDay, int age, String address) {
        return isValidName(name) && isValidName(lastName) && isValidEmail(email) && isValidBirthDay(birthDay) && isValidAge(age) && isValidAddress(address);
    }
*/
}
