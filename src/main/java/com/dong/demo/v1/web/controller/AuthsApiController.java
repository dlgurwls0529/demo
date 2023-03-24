package com.dong.demo.v1.web.controller;

import com.dong.demo.v1.web.dto.*;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AuthsApiController {

    @GetMapping("/api/v1/write-auths/{accountCP}/folders")
    public ResponseEntity<List<WriteAuthsGetResponseDto>> getWriteAuthByAccountCP(
            @PathVariable String accountCP
    ) {
        WriteAuthsGetResponseDto dto =
                WriteAuthsGetResponseDto.builder()
                        .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                        .folderPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqTKHrhh/X1rH6RN759oWA/7xEGj9pJEqzpwawagCVlcK52U7RMocObxROK86ZU8Yk4kweyxPThnVS8JqKGTWcZALnGImviHqONfP8kZtAFqsdyg8tfPbRaHpVxhrkDh/6y5aXpETSmQA5TQllfg5dAPDlrA9AUOsZdgxvd2Pv3+aYmrdfFVr6J6BFjm6MLCutfsfV9/wAZB86/BxWbxqTEqGtUHhGQ5mDAIvP1Ym3xoEuDRWwlFZ48o4ZmVMB8PCYiBxHwCRJBxEBKRhIQ9BKXtdC3INGfRQgGDANlifBaKyZ2JN/dUzGaQx5LLpTqZK2lDoLrC+CSY0apt3Az3ZCQIDAQAB")
                        .folderPrivateKeyEWA("[123, 219, 198, 9, 170, 254, 200, 2, 17, 98, 118, 35, 145, 132, 78, 242, 9, 38, 45, 103, 131, 27, 54, 10, 210, 117, 90, 108, 227, 126, 70, 223, 113, 236, 64, 199, 200, 44, 218, 36, 146, 2, 183, 86, 50, 246, 103, 200, 20, 243, 155, 198, 194, 20, 186, 249, 65, 69, 43, 246, 125, 217, 22, 59, 38, 55, 96, 200, 84, 172, 25, 251, 71, 5, 11, 227, 208, 185, 44, 205, 10, 177, 64, 14, 56, 46, 191, 29, 167, 74, 163, 197, 172, 78, 89, 39, 1, 128, 185, 44, 29, 234, 56, 69, 214, 4, 74, 66, 153, 159, 54, 28, 117, 252, 136, 175, 152, 1, 254, 80, 15, 8, 27, 173, 254, 16, 245, 243, 50, 0, 100, 136, 104, 0, 192, 179, 153, 221, 75, 13, 45, 95, 17, 80, 111, 139, 95, 224, 167, 37, 210, 206, 15, 37, 244, 155, 172, 64, 239, 252, 74, 197, 161, 242, 63, 206, 39, 11, 189, 119, 176, 137, 91, 245, 188, 206, 175, 82, 164, 160, 43, 165, 168, 103, 163, 129, 245, 24, 243, 161, 103, 247, 45, 148, 21, 59, 141, 169, 42, 228, 150, 73, 234, 153, 82, 161, 209, 198, 191, 5, 144, 231, 254, 127, 19, 119, 125, 57, 90, 67, 54, 6, 6, 191, 92, 69, 163, 183, 75, 133, 166, 233, 235, 254, 195, 254, 233, 126, 226, 151, 7, 41, 3, 184, 223, 192, 157, 171, 93, 244, 157, 175, 138, 19, 110, 22, 110, 116, 194, 122, 34, 5, 31, 57, 200, 254, 125, 188, 97, 166, 242, 193, 76, 187, 227, 1, 12, 244, 207, 172, 138, 108, 14, 254, 180, 108, 114, 182, 93, 12, 232, 30, 46, 138, 238, 253, 16, 100, 153, 148, 170, 251, 143, 139, 64, 236, 192, 254, 132, 9, 107, 133, 63, 8, 93, 203, 124, 104, 20, 107, 206, 90, 10, 248, 162, 162, 33, 230, 84, 224, 77, 219, 36, 112, 13, 101, 205, 41, 238, 103, 241, 18, 183, 203, 162, 102, 65, 208, 212, 251, 156, 255, 146, 91, 34, 128, 90, 119, 18, 218, 35, 15, 250, 186, 61, 244, 164, 43, 248, 240, 185, 77, 197, 210, 60, 250, 58, 120, 165, 250, 178, 4, 118, 238, 102, 11, 32, 250, 150, 144, 27, 1, 251, 173, 72, 221, 75, 73, 246, 111, 247, 104, 86, 169, 224, 66, 248, 57, 154, 142, 81, 142, 121, 53, 9, 217, 230, 166, 211, 147, 195, 133, 157, 121, 42, 178, 201, 242, 240, 229, 117, 56, 49, 176, 33, 178, 221, 216, 238, 180, 120, 198, 149, 252, 112, 0, 16, 195, 131, 158, 226, 39, 241, 174, 6, 118, 166, 23, 32, 207, 35, 90, 115, 193, 92, 97, 62, 195, 244, 234, 43, 72, 205, 248, 139, 82, 119, 214, 142, 91, 169, 150, 244, 139, 120, 230, 159, 10, 128, 244, 137, 137, 103, 37, 126, 154, 108, 7, 74, 65, 172, 81, 227, 98, 140, 149, 205, 255, 205, 67, 133, 103]")
                        .isTitleOpen(true)
                        .title("Crypto")
                        .symmetricKeyEWF("symmetrickeyEWF_TEST")
                        .lastChangedDate(LocalDateTime.now())
                        .build();

        List<WriteAuthsGetResponseDto> list = new ArrayList<>();
        list.add(dto);
        list.add(dto);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/api/v1/read-auths/{accountCP}/folders")
    public ResponseEntity<List<ReadAuthsGetResponseDto>> getReadAuthByAccountCP(
            @PathVariable String accountCP) {
        ReadAuthsGetResponseDto dto =
                ReadAuthsGetResponseDto.builder()
                        .folderCP("eUUGcJRYmP4ijNYFetClY0Ju7ifLqGEamuoK/4so+/Q=")
                        .isTitleOpen(true)
                        .title("Hungry")
                        .symmetricKeyEWA("symmetricKeyEWA_TEST")
                        .lastChangedDate(LocalDateTime.now())
                        .build();

        List<ReadAuthsGetResponseDto> list = new ArrayList<>();
        list.add(dto);
        list.add(dto);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/api/v1/write-auths")
    public ResponseEntity<Void> addWriteAuthority(@RequestBody WriteAuthsAddRequestDto dto) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
