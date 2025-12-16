## 1. Transaction Verification Endpoint

**Purpose:** Verifies if a customer exists securely during a transaction.  
**URL:** `/api/v1/customer/details`  
**Method:** `POST`  
**Caller:** External Client / User  
**Input:** RSA Encrypted Payload

### Process Flow

#### Step 1: Request Reception

The `CustomerController` receives the RSA-encrypted request and delegates processing to the `CustomerDetailsService`.

#### Step 2: Decryption (RSA)

The `CustomerDetailsService` executes `processEncryptedRequest`:

1. Accepts the raw encrypted payload.
2. Calls `RsaService.decrypt()` using the system's **Private Key** (from `keys/private_key.pem`).
3. **Result:** Plain text JSON string.

#### Step 3: Data Extraction

The service uses `ObjectMapper` (Jackson) to parse the JSON and extract the **card number**.

#### Step 4: Re-Encryption (AES)

To query the database securely:

1. Calls `AesService.encrypt()` with the plain card number.
2. **Result:** AES-encrypted card string (matching the DB format).

#### Step 5: Database Verification

The service calls `repository.findByEncryptedCardNumber(aesEncryptedCard)`.

#### Step 6: Final Decision

- **If Found:** Returns `ApiResponse` DTO with success `200 OK`.
- **If Not Found:** Declines transaction; returns error.

---

## 2. Customer Onboarding Endpoint

**Purpose:** Persists new customer data into the system.  
**URL:** `/api/v1/customer/persist` **Method:** `POST`  
**Caller:** Internal Service (Trusted)  
**Input:** Unencrypted Customer Payload (JSON)

### Process Flow

#### Step 1: Internal Request Reception

An internal service calls this endpoint with raw, unencrypted customer data. Since this is an internal call, RSA decryption is **not** required.

#### Step 2: Service Invocation

The `CustomerController` calls the `CustomerPersistenceService`.

#### Step 3: Data Persistence

The service executes the `persistCustomerData` method:

1. It persists the new customer record.

#### Step 4: Response

Upon successful insertion:

1. The database confirms the commit.
2. The service creates a `CustomerPersistanceResponse` DTO.
3. The controller returns the DTO to the calling internal service.
