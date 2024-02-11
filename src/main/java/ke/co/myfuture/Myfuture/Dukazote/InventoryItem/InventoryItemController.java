package ke.co.myfuture.Myfuture.Dukazote.InventoryItem;

import ke.co.myfuture.Myfuture.Utils.Response.UniversalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("inventoryItem")
public class InventoryItemController {
    @Autowired
    InventoryItemRepository repository;

    @PostMapping("add/")
    public ResponseEntity<?> newInventoryItem(@RequestBody InventoryItem inventoryItem) {
        InventoryItem savedInventoryItem = repository.save(inventoryItem);
        System.out.println(savedInventoryItem);
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Saved successfully");
        response.setEntity(savedInventoryItem);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("update/")
    public ResponseEntity<?> updateInventoryItem(@RequestBody InventoryItem inventoryItem) {
        InventoryItem updatedInventoryItem = repository.save(inventoryItem);

        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("Updated Successfully");
        response.setEntity(updatedInventoryItem);
        response.setStatusCode(201);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("get/by/id")
    public ResponseEntity<?> fetchInventoryItem(@RequestParam("id") Long id) {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("InventoryItem retrieved Successfully");
        response.setEntity(repository.findById(id));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("all")
    public ResponseEntity<?> fetchProductCategory() {
        UniversalResponse response = new UniversalResponse();
        response.setStatus("Success");
        response.setMessage("ProductCategory retrieved Successfully");
        response.setEntity(repository.findAllByAuditTrails_DeletedFlag(false));
        response.setStatusCode(200);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
