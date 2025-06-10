package fr.toitoing.contactapi.resource;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;
import static fr.toitoing.contactapi.constantes.Constante.DOSSIER_PHOTO;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import fr.toitoing.contactapi.domain.Contact;
import fr.toitoing.contactapi.repo.ContactRepo;
import fr.toitoing.contactapi.service.ContactService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactResource {
    private final ContactService serviceContacts;
    private final ContactRepo contactRepo;

    @PostMapping
    public ResponseEntity<Contact> creerContact(@RequestBody Contact contact) {
        return ResponseEntity.created(URI.create("/contacts/userID")).body(serviceContacts.createContact(contact));
    }

    @GetMapping
    public ResponseEntity<Page<Contact>> getContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok().body(serviceContacts.getAllContacts(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok().body(serviceContacts.getContactById(id));
    }

    @PutMapping("/photo")
    public ResponseEntity<String> chargerPhoto( @RequestParam(value = "id") String id,
                                                @RequestParam("fichierImage") MultipartFile fichierImage) {
        return ResponseEntity.ok().body(serviceContacts.chargerPhoto(id, fichierImage));
    }

    
    @GetMapping(path = "/image/{filename}", produces = { IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE })
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {
        return Files.readAllBytes(Paths.get(DOSSIER_PHOTO + filename));
    }

    @DeleteMapping("/{id}")
    public void deleteContact(@PathVariable(value = "id") String id)  {
        contactRepo.deleteById(id);
    }

}
