package org.LegoService;

import javax.persistence.Table;
import java.sql.Date;
import java.util.List;


@Table(name = "mieiOridni")
public class mieiOrdini {
    public int NumeroOrdine;
    public String Stato;
    public Date DataOrdine;

}
