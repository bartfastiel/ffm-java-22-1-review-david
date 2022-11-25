package capstone.storage.backend;

import capstone.storage.backend.exceptions.ItemAlreadyExistException;
import capstone.storage.backend.models.AddItemDto;
import capstone.storage.backend.models.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/items/")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @GetMapping
    public List<Item> getAllItems() {
        return service.findAll();
    }

    @PostMapping("{eanToFind}")
    @ResponseStatus(HttpStatus.CREATED)
    public Item saveItem(@PathVariable String eanToFind, @RequestBody AddItemDto addItemDto) {

        if (addItemDto.ean().equals(eanToFind)) {

            if (service.isNullOrEmpty(addItemDto)) {
                throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED);
            }
            try {
                return service.addItem(addItemDto, eanToFind);
            } catch (ItemAlreadyExistException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED);
            }
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
    @PutMapping("{id}")
    public ResponseEntity<Item> updateItem(@PathVariable String id, @RequestBody Item itemToUpdate) {
        if (itemToUpdate.id().equals(id)) {
            boolean itemExist = service.existById(id);
            Item updatedItem = service.updateItem(itemToUpdate);
            return itemExist ? ResponseEntity.status(HttpStatus.OK).body(updatedItem) : new ResponseEntity<>(HttpStatus.CREATED);
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable String id) {
        if (!service.existById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        service.deleteItemById(id);
    }

}
