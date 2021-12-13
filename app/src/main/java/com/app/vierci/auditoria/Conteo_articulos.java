package com.app.vierci.auditoria;


public class Conteo_articulos {


    int id,position;
    private String articulo,codigo,conteo, hora;

    public Conteo_articulos(int id, String articulo,String codigo,String conteo, String hora)
    {
        setId(id);
        setArticulo(articulo);
        setCodigo(codigo);
        setConteo(conteo);
        setHora(hora);
    }

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getConteo() {
        return conteo;
    }

    public void setConteo(String conteo) {
        this.conteo = conteo;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int id) {
        this.position = position;
    }


}
