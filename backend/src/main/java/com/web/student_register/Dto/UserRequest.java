package com.web.student_register.Dto;


import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRequest {
    private String name;
    @NotBlank(message="User name can not be empty")
    @NotNull(message = "Username cannot be null")
    @Pattern(regexp="[a-zA-Z]*", message="userName can have only letters")
    private String userName;
    @NotBlank(message="Password can not be empty")
    @NotNull(message = "Password cannot be null")
    @Size(min=8, max=20, message="Password must be between 8 and 20 character")
    @Pattern(regexp="^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$", message="Invalid password! Password must be at least 8 characters long and include uppercase letters, lowercase letters, numbers, and special characters.")
    private String password;
    private String roleName;

}
