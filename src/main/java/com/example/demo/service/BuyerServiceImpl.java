
package com.example.demo.service;

import com.example.demo.entity.Buyer;
import com.example.demo.repository.BuyerRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class BuyerServiceImpl implements BuyerService {

    private final BuyerRepository buyerRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    @Value("${app.upload.dir:uploads/buyer-profiles}")
    private String uploadDirectory;

    public BuyerServiceImpl(BuyerRepository buyerRepository) {
        this.buyerRepository = buyerRepository;
    }

    // ================= REGISTER BUYER =================

    @Override
    public Buyer register(Buyer buyer, MultipartFile profilePhoto) {

        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Buyer information is required.");
        }

        if (buyer.getFullName() == null
                || buyer.getFullName().isBlank()) {
            throw new IllegalArgumentException(
                    "Full name is required.");
        }

        if (buyer.getEmail() == null
                || buyer.getEmail().isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required.");
        }

        String email = buyer.getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (buyerRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "This email is already registered.");
        }

        if (buyer.getPassword() == null
                || buyer.getPassword().length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters.");
        }

        validateQuantityAndPrice(buyer);

        buyer.setFullName(buyer.getFullName().trim());
        buyer.setEmail(email);
        buyer.setPassword(
                passwordEncoder.encode(buyer.getPassword()));

        if (buyer.getPhone() != null) {
            buyer.setPhone(buyer.getPhone().trim());
        }

        if (profilePhoto != null && !profilePhoto.isEmpty()) {
            try {
                buyer.setProfilePhoto(
                        saveProfilePhoto(profilePhoto));
            } catch (IOException e) {
                throw new IllegalArgumentException(
                        "Unable to save profile photo.", e);
            }
        }

        return buyerRepository.save(buyer);
    }

    // ================= BUYER LOGIN =================

    @Override
    @Transactional(readOnly = true)
    public Buyer login(String email, String password) {

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Email and password are required.");
        }

        Buyer buyer = buyerRepository
                .findByEmailIgnoreCase(email.trim())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password."));

        if (!passwordEncoder.matches(
                password, buyer.getPassword())) {
            throw new IllegalArgumentException(
                    "Invalid email or password.");
        }

        return buyer;
    }

    // ================= FIND BUYER BY EMAIL =================

    @Override
    @Transactional(readOnly = true)
    public Buyer getBuyerByEmail(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required.");
        }

        return buyerRepository
                .findByEmailIgnoreCase(email.trim())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Buyer not found."));
    }

    // ================= FIND BUYER BY ID =================

    @Override
    @Transactional(readOnly = true)
    public Buyer getBuyerById(Long id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Buyer ID is required.");
        }

        return buyerRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Buyer not found."));
    }

    // ================= GET ALL BUYERS =================

    @Override
    @Transactional(readOnly = true)
    public List<Buyer> getAllBuyers() {
        return buyerRepository.findAll();
    }

    // ================= SAVE BUYER =================

    @Override
    public Buyer saveBuyer(Buyer buyer) {

        if (buyer == null) {
            throw new IllegalArgumentException(
                    "Buyer information is required.");
        }

        validateQuantityAndPrice(buyer);

        return buyerRepository.save(buyer);
    }

    // ================= UPDATE BUYER PROFILE =================

    @Override
    public Buyer updateBuyerProfile(
            String email, Buyer updatedBuyer) {

        if (updatedBuyer == null) {
            throw new IllegalArgumentException(
                    "Buyer information is required.");
        }

        Buyer existing = getBuyerByEmail(email);

        if (updatedBuyer.getFullName() != null) {
            if (updatedBuyer.getFullName().isBlank()) {
                throw new IllegalArgumentException(
                        "Full name cannot be empty.");
            }
            existing.setFullName(
                    updatedBuyer.getFullName().trim());
        }

        if (updatedBuyer.getPhone() != null)
            existing.setPhone(updatedBuyer.getPhone().trim());

        if (updatedBuyer.getAlternativePhone() != null)
            existing.setAlternativePhone(
                    updatedBuyer.getAlternativePhone().trim());

        if (updatedBuyer.getDateOfBirth() != null)
            existing.setDateOfBirth(updatedBuyer.getDateOfBirth());

        if (updatedBuyer.getVillage() != null)
            existing.setVillage(updatedBuyer.getVillage());

        if (updatedBuyer.getMandal() != null)
            existing.setMandal(updatedBuyer.getMandal());

        if (updatedBuyer.getDistrict() != null)
            existing.setDistrict(updatedBuyer.getDistrict());

        if (updatedBuyer.getState() != null)
            existing.setState(updatedBuyer.getState());

        if (updatedBuyer.getPincode() != null)
            existing.setPincode(updatedBuyer.getPincode());

        if (updatedBuyer.getBusinessName() != null)
            existing.setBusinessName(
                    updatedBuyer.getBusinessName());

        if (updatedBuyer.getBuyerType() != null)
            existing.setBuyerType(updatedBuyer.getBuyerType());

        if (updatedBuyer.getBusinessAddress() != null)
            existing.setBusinessAddress(
                    updatedBuyer.getBusinessAddress());

        if (updatedBuyer.getGstNumber() != null)
            existing.setGstNumber(updatedBuyer.getGstNumber());

        if (updatedBuyer.getCropInterested() != null)
            existing.setCropInterested(
                    updatedBuyer.getCropInterested());

        if (updatedBuyer.getRequiredQuantity() != null)
            existing.setRequiredQuantity(
                    updatedBuyer.getRequiredQuantity());

        if (updatedBuyer.getQuantityUnit() != null)
            existing.setQuantityUnit(
                    updatedBuyer.getQuantityUnit());

        if (updatedBuyer.getPreferredPrice() != null)
            existing.setPreferredPrice(
                    updatedBuyer.getPreferredPrice());

        if (updatedBuyer.getLatitude() != null)
            existing.setLatitude(updatedBuyer.getLatitude());

        if (updatedBuyer.getLongitude() != null)
            existing.setLongitude(updatedBuyer.getLongitude());

        validateQuantityAndPrice(existing);

        // Email, password, Aadhaar, and profile photo are
        // deliberately not changed by this operation.
        return buyerRepository.save(existing);
    }

    // ================= CHANGE BUYER PASSWORD =================

    @Override
    public void changeBuyerPassword(
            String email,
            String currentPassword,
            String newPassword,
            String confirmPassword) {

        if (email == null || email.isBlank()
                || currentPassword == null
                || currentPassword.isBlank()
                || newPassword == null
                || confirmPassword == null) {
            throw new IllegalArgumentException(
                    "All password fields are required.");
        }

        Buyer buyer = getBuyerByEmail(email);

        if (!passwordEncoder.matches(
                currentPassword, buyer.getPassword())) {
            throw new IllegalArgumentException(
                    "Current password is incorrect.");
        }

        if (newPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "New password must contain at least 8 characters.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException(
                    "New password and confirmation do not match.");
        }

        if (passwordEncoder.matches(
                newPassword, buyer.getPassword())) {
            throw new IllegalArgumentException(
                    "New password must differ from the current password.");
        }

        buyer.setPassword(passwordEncoder.encode(newPassword));
        buyerRepository.save(buyer);
    }

    // ================= VALIDATE QUANTITY AND PRICE =================

    private void validateQuantityAndPrice(Buyer buyer) {

        BigDecimal quantity = buyer.getRequiredQuantity();

        if (quantity != null
                && quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Required quantity must be greater than zero.");
        }

        BigDecimal price = buyer.getPreferredPrice();

        if (price != null
                && price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Preferred price cannot be negative.");
        }
    }

    // ================= SAVE PROFILE PHOTO =================

    @Override
    public String saveProfilePhoto(MultipartFile file)
            throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select a profile photo.");
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !List.of(
                        "image/jpeg",
                        "image/png",
                        "image/webp"
                ).contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException(
                    "Only JPG, PNG, and WEBP images are allowed.");
        }

        if (file.getSize() > 5L * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "Profile photo must not exceed 5 MB.");
        }

        String extension;

        switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg":
                extension = ".jpg";
                break;
            case "image/png":
                extension = ".png";
                break;
            case "image/webp":
                extension = ".webp";
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported profile photo format.");
        }

        Path directory = Paths.get(uploadDirectory)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(directory);

        String fileName = UUID.randomUUID() + extension;

        Path destination = directory.resolve(fileName).normalize();

        if (!destination.startsWith(directory)) {
            throw new IllegalArgumentException(
                    "Invalid profile photo path.");
        }

        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination);
        }

        return destination.toString();
    }
}
