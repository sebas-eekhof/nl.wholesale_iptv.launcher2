package nl.wholesale_iptv.launcher2.models;

public class UpdateItem {
    public final String name;
    public final String package_id;
    public final String current_version;
    public final String available_version;
    public final int current_build_id;
    public final int available_build_id;
    public final String url;

    public UpdateItem(String name, String package_id, String current_version, String available_version, int current_build_id, int available_build_id, String url) {
        this.name = name;
        this.package_id = package_id;
        this.current_version = current_version;
        this.available_version = available_version;
        this.current_build_id = current_build_id;
        this.available_build_id = available_build_id;
        this.url = url;
    }

    public boolean hasUpdate() {
        return (current_build_id != available_build_id);
    }
}
