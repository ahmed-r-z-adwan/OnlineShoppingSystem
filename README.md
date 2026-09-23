# Online Shopping System

An Android shopping app with two roles behind one login: shoppers browse a catalogue and check out, admins manage what is in it. Kotlin, with Firebase Authentication and Realtime Database behind it.

## What it does

**As a shopper** — sign up or sign in, browse categories, open a category to see its products, open a product for details, add it to a cart, and check out.

**As an admin** — the same login lands on the management screens instead: create and edit categories, create and edit products, attach a product image.

Which set of screens you get is decided at sign-in by reading `users/$uid/role` from the database.

## Screens

| Flow | Activities |
|---|---|
| Auth | `LoginActivity`, `RegisterActivity` |
| Shopper | `CustomerCategoryActivity`, `CustomerProductListActivity`, `CustomerProductDetailsActivity`, `CartActivity`, `CheckoutActivity` |
| Admin | `CategoryListActivity`, `CategoryFormActivity`, `ProductListActivity`, `ProductFormActivity` |

## Data

```
users/$uid              → uid, name, email, role ("customer" | "admin")
categories/$id          → id, name, imageBase64
products/$id            → id, categoryId, categoryName, name, description,
                          imageBase64, price, rate, location
carts/$uid/$itemId      → productId, userId, name, price, quantity
orders/$uid/$orderId    → userId, totalAmount, status, timestamp
```

Images are held **as Base64 strings inside the database**, not as URLs into Cloud Storage. That keeps the upload path simple but it does not scale: every category or product list pulls the full image bytes down with the row, and the Realtime Database caps a single node at 10 MB. Moving images to Storage is the first thing worth changing.

## Security

The role check in `LoginActivity` runs **on the device**. It decides which screens to show, which is a convenience — not a control. Anyone holding the app's configuration can talk to the database directly and never run that code, so the real limits have to live on the server.

**[`database.rules.json`](database.rules.json)** puts them there: catalogue readable by any signed-in user and writable only by an admin, a cart readable and writable only by its owner, an order writable only by the shopper it belongs to and readable by them or an admin. Field-level `.validate` rules pin the shape of every record, so a write cannot invent fields or put a string where a price belongs.

Two parts are worth reading rather than skimming:

- **Self-promotion to admin.** The owner needs `.write` on `users/$uid`, because `setValue()` writes the whole object at once and a rule on a child cannot authorise a write to its parent. So the restriction lives in a `.validate` on `role`, which *does* apply to the children of a write: a user may write `customer`, and only an existing admin may write `admin`.

- **The first admin.** `RegisterActivity` grants `admin` to whoever signs up with a specific hard-coded email address. That is a backdoor at a published address, and under these rules it stops working — the write is rejected because the account is not an admin yet. Promote the first admin by editing `users/$uid/role` in the Firebase console, and the door closes.

```bash
firebase deploy --only database
```

**The rules have not been deployed or tested against a live project.** Until they are, whatever the Firebase console currently holds is what is in force — and a project left in test mode is open to anyone.

### On the committed config

`app/google-services.json` is in the repository. That is normal for an Android Firebase app: it carries the project id, the app id and a client API key, all of which ship inside any installed APK anyway and none of which grant access on their own. What it does mean is that the database is exactly as safe as its rules.

## Running it

The committed config points at the original Firebase project, which you will not have access to. To run your own copy:

1. Create a Firebase project and add an Android app with the application id `com.example.onlineshoppingsystem`
2. Replace `app/google-services.json` with the one it gives you
3. Enable Email/Password authentication and the Realtime Database
4. Deploy the rules above
5. Register an account, then set its `role` to `admin` in the console to reach the admin screens

```bash
./gradlew assembleDebug
```

Build with **JDK 21**. The wrapper here is Gradle 8.13, which cannot read a Java 25
version string and fails with the bare version number as its whole error message —
so point `JAVA_HOME` at a 21 rather than whatever your IDE happens to bundle.

## Built with

Kotlin · Firebase Auth · Realtime Database · RecyclerView · Glide · Material Components · View Binding

## Known limitations

- **No automated tests.** The only test file is the one the project template generated.
- **Images as Base64 in the database**, as described above.
- `firebase-storage` is still declared in `app/build.gradle.kts` although no code uses it.
- Checkout records an order but takes no payment.
- The package is still `com.example.onlineshoppingsystem`, from the project template.
