package FastRailStation.model;

/**
 * Ruolo dell'utente nel sistema.
 * Sostituisce il controllo hardcoded "admin/admin" in LoginController.
 */
public enum Ruolo {
    ADMIN,
    USER;

    /** Parsing sicuro: restituisce USER per qualsiasi stringa non riconosciuta. */
    public static Ruolo from(String s) {
        if (s == null) return USER;
        try { return valueOf(s.trim().toUpperCase()); }
        catch (IllegalArgumentException e) { return USER; }
    }
}