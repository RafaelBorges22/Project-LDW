package ldw.squad.project.Dto;

public record ResetPasswordDto (
        String token,
        String newPassword
        ){
}
