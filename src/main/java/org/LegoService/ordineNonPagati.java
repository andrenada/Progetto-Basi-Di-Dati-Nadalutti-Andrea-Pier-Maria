package org.LegoService;

import javax.persistence.Table;
import java.sql.Date;


@Table(name = "ordineNonPagati")
public class ordineNonPagati {
    public int NumeroOrdine;
    public String Username;
    public String Stato;
    public Date DataOrdine;
}

