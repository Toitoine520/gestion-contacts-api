package fr.toitoing.contactapi.service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.BiFunction;
import java.util.function.Function;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import fr.toitoing.contactapi.domain.Contact;
import fr.toitoing.contactapi.repo.ContactRepo;
import static fr.toitoing.contactapi.constantes.Constante.DOSSIER_PHOTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepo contactRepo;

    public Page<Contact> getAllContacts(int page, int size) {
        return contactRepo.findAll(PageRequest.of(page, size, Sort.by("nom")));
    };

    public Contact getContactById(String id) {
        return contactRepo.findById(id).orElseThrow(() -> new RuntimeException("Contact introuvable"));
    }

    public Contact createContact(Contact contact){
        return contactRepo.save(contact);
    }
    
    public void deleteContact(Contact contact) {
        // Supprimer de partout dans l'application
    }

    public String chargerPhoto(String id, MultipartFile fichier)
    {
        log.info("Image en cours d'enregistrement pour l'utilisateur: {}", id);
        Contact contact = getContactById(id);
        String photoUrl = photoFunction.apply(id, fichier);

        contact.setPhotoUrl(photoUrl);
        contactRepo.save(contact);
        return photoUrl;

    }

    private final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains("."))
            .map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");

    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        String filename = id + fileExtension.apply(image.getOriginalFilename());
        try {
            Path fileStorageLocation = Paths.get(DOSSIER_PHOTO).toAbsolutePath().normalize();
            if(!Files.exists(fileStorageLocation)) { Files.createDirectories(fileStorageLocation); }
            Files.copy(image.getInputStream(), fileStorageLocation.resolve(filename), REPLACE_EXISTING);
            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/contacts/image/" + filename).toUriString();
        }catch (Exception exception) {
            throw new RuntimeException("Unable to save image");
        }
    };
}
