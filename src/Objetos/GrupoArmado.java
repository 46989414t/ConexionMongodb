package Objetos;

import java.util.ArrayList;

/**
 * Created by enric on 20/4/16.
 */
public class GrupoArmado {
    private int id;
    private String nombre;
    private int bajas;
    private ArrayList<Conflicto> listaConflicto = new ArrayList<>();

    public GrupoArmado() {
    }

    public GrupoArmado(int id, String nombre, int bajas) {
        this.id = id;
        this.nombre = nombre;
        this.bajas = bajas;
    }

    public int getBajas() {
        return bajas;
    }

    public void setBajas(int bajas) {
        this.bajas = bajas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<Conflicto> getListaConflicto() {
        return listaConflicto;
    }

    public void setListaConflicto(ArrayList<Conflicto> listaConflicto) {
        this.listaConflicto = listaConflicto;
    }

    @Override
    public String toString() {
        String devolver = "id: "+id+", Nombre: " + nombre+ ", Bajas: "+bajas+'\n';

        return devolver;
    }
}
