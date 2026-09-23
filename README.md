# Online Shopping System

An Android shopping app with two roles behind one login: shoppers browse a catalogue and check out, admins manage what is in it. Kotlin, with Firebase Authentication, Realtime Database and Storage behind it.

## What it does

**As a shopper** — sign up or sign in, browse categories, open a category to see its products, open a product for details, add it to a cart, and check out.

**As an admin** — the same login lands on the management screens instead: create and edit categories, create and edit products, upload a product image.

Which set of screens you get is decided at sign-in by reading `users/$uid/role` from the database.

## Screens

| Flow | Activities |
|---|---|
| Auth | `LoginActivity`, `RegisterActivity` |
| Shopper | `CustomerCategoryActivity`, `CustomerProductListActivity`, `CustomerProductDetailsActivity`, `CartActivity`, `CheckoutActivity` |
| Admin | `CategoryListActivity`, `CategoryFormActivity`, `ProductListActivity`, `ProductFormActivity` |

## Data

```
users/$uid      → name, email, role ("customer" | "admin")
categories/     → the catalogue's top level
products/       → belong to a category, carry an image URL from Storage
carts/$uid      → the signed-in shopper's own cart
orders/         → placed at checkout
```

## Security

The role check in `LoginActivity` runs **on the device**. It decides which screens to show, which is a convenience — not a control. Anyone holding the app's configuration can talk to the database directly and never run that code, so the real limits have to live on the server.

This repository ships the rules that express them:

- **[`database.rules.json`](database.rules.json)** — catalogue readable by any signed-in user and writable only by an admin; a cart readable and writable only by its owner; an order visible to the shopper who placed it and to admins.

  The users rule is the part worth reading. It grants no `.write` at the `users/$uid` level, because in Realtime Database a granted write cascades to everything beneath it — a single blanket rule there would let any signed-in user set their own role to `admin`. Permission is granted field by field instead, and `role` can only be written as `customer`, once, at sign-up.

- **[`storage.rules`](storage.rules)** — product images readable by signed-in users, uploads bounded by size and content type. Storage rules cannot read the Realtime Database, so restricting uploads to admins needs a custom claim on the auth token; the file says so rather than pretending otherwise.

```bash
firebase deploy --only database,storage
```

**These rules are written but not yet deployed, and they have not been tested against a live project.** Until they are, whatever rules the Firebase console currently holds are the ones in force — and a project left in test mode is open to anyone.

### On the committed config

`app/google-services.json` is in the repository. That is normal for an Android Firebase app: it carries the project id, the app id and a client API key, all of which ship inside any installed APK anyway and none of which grant access on their own. What it does mean is that the database is exactly as safe as its rules, which is why the two files above matter.

## Running it

The committed config points at the original Firebase project, which you will not have access to. To run your own copy:

1. Create a Firebase project, add an Android app with the application id `com.example.onlineshoppingsystem`
2. Replace `app/google-services.json` with the one it gives you
3. Enable Email/Password authentication, Realtime Database and Storage
4. Deploy the rules above
5. Add a user and set their `role` to `admin` by hand to reach the admin screens

```bash
./gradlew assembleDebug
```

## Built with

Kotlin · Firebase Auth · Realtime Database · Cloud Storage · RecyclerView · Glide · Material Components · View Binding

## Known limitations

- No automated tests; the only test file is the Android template's
- Checkout records an order but does not take payment
- The package is still `com.example.onlineshoppingsystem`, from the project template
