package com.mach.core.util;

public enum EnumCardDeliveryStatus {
    STATUS_001_ISSUED("ISSUED","Validando solicitud", "Recibimos tu solicitud y la estamos procesando. Puede tardar máximo 2 días hábiles."),
    STATUS_CARD_MANUFACTURER_RECEIVED("CARD_MANUFACTURER_RECEIVED", "Verificando cobertura", ""),
    STATUS_002_VALID_ADDRESS("VALID_ADDRESS","Preparando tu tarjeta", "Una vez lista la entregaremos a nuestros correos y la enviaremos a donde nos pediste."),
    STATUS_COURIER_RECEIVED("COURIER_RECEIVED", "Entregada a correos", ""),
    STATUS_NOT_DELIVERED("NOT_DELIVERED", "No entregada", ""),
    STATUS_INVALID_ADDRESS("INVALID_ADDRESS", "Problemas con tu dirección", ""),
    STATUS_CARD_PACKAGING_VAULT("CARD_PACKAGING_VAULT", "No entregada", ""),
    STATUS_003_DISPATCHED("DISPATCHED", "Tarjeta en camino", "Confirma al recibirla"),
    STATUS_004_DELIVERED("DELIVERED", "Confirma al recibirla", "Si tu tarjeta ya está en tus manos, comienza la activación a continuación."),
    STATUS_INCONSISTENT_APPLICATION("INCONSISTENT_APPLICATION", "Servicio no disponible", ""),
    STATUS_DESTROYED("DESTROYED", "Servicio no disponible", "");

    private String constDB;
    private String txtInHomeCards;
    private String extraText;

    EnumCardDeliveryStatus (String constDB, String txtInHomeCards, String extraText) {
        this.constDB = constDB;
        this.txtInHomeCards = txtInHomeCards;
        this.extraText = extraText;
    }

    public String getConstDB() {
        return this.constDB;
    }
    public String getTxtInHomeCards() {
        return this.txtInHomeCards;
    }
    public String getExtraText() {return this.extraText;}

}
