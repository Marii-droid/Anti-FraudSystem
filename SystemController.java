package antifraud;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/antifraud")
public class SystemController {
    @GetMapping("/transaction")
    public ResponseEntity<String> getTransaction() {
        return null;
    }

    @PostMapping("/transaction")
    public ResponseEntity<TransactionResponse> payAmount(@RequestBody AmountRequest input) {
        long amount = input.amount;

        if (amount <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (amount <= 200) {
            return new ResponseEntity<>(new TransactionResponse("ALLOWED"), HttpStatus.OK);
        } else if (amount <= 1500) {
            return new ResponseEntity<>(new TransactionResponse("MANUAL_PROCESSING"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new TransactionResponse("PROHIBITED"), HttpStatus.OK);
        }
    }

    public record AmountRequest(long amount) {}
    public record TransactionResponse(String result) {}
}

