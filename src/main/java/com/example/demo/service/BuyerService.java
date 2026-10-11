
package com.example.demo.service;

import com.example.demo.entity.Buyer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BuyerService {

    Buyer register(Buyer buyer, MultipartFile profilePhoto);

    Buyer login(String email, String password);

    Buyer getBuyerByEmail(String email);

    Buyer getBuyerById(Long id);

    List<Buyer> getAllBuyers();

    Buyer saveBuyer(Buyer buyer);

    String saveProfilePhoto(MultipartFile profilePhoto)
            throws IOException;

    Buyer updateBuyerProfile(String email, Buyer updatedBuyer);

    void changeBuyerPassword(
        String email,
        String currentPassword,
        String newPassword,
        String confirmPassword
    );
}
