package SGBD_Project.example.LearnCode.Dto;
import SGBD_Project.example.LearnCode.Models.PostUserAction;
import SGBD_Project.example.LearnCode.Models.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String id;
    private String name;
    private String lastName;
    private String email;
    private LocalDate birthDay;
    private String address;
    private Boolean isBanned;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String token;
    private Set<PostUserAction> postUserActions = new HashSet<>();

    public static UserDto userToDto(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .birthDay(userEntity.getBirthDay())
                .address(userEntity.getAddress())
                .isBanned(userEntity.getIsBanned())
                .createdDate(userEntity.getCreatedDate())
                .updatedDate(userEntity.getUpdatedDate())
                .build();
    }
    public static UserDto userToDtoWithToken(UserEntity userEntity,String token) {
        return UserDto.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .birthDay(userEntity.getBirthDay())
                .address(userEntity.getAddress())
                .isBanned(userEntity.getIsBanned())
                .createdDate(userEntity.getCreatedDate())
                .updatedDate(userEntity.getUpdatedDate())
                .token(token)
                .build();
    }

    public static UserEntity dtoToUser(UserDto userDto) {
        return UserEntity.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .birthDay(userDto.getBirthDay())
                .address(userDto.getAddress())
                .isBanned(userDto.getIsBanned())
                .createdDate(userDto.getCreatedDate())
                .updatedDate(userDto.getUpdatedDate())
                .build();
    }
}
