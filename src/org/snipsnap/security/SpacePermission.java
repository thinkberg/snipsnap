public class SpacePermission extends java.security.Permission {

    /** Creates a new instance of URLPermission */
    public SpacePermission(String name) {
        super(name);
    }

    public void checkGuard(Object object) {
    }

    public boolean equals(Object obj) {
        if((obj instanceof SpacePermission) && ((SpacePermission)obj).getName().equals(this.getName()))
            return true;
        else
            return false;
    }

    public String getActions() {
        //we do not have any actions int this class;just allow or deny acces to URL
        return "";
    }

    public int hashCode() {
        return this.getName().hashCode(); //if we have any actions it should be: getName().hashCode() ^ acion_mask
    }

    public boolean implies(java.security.Permission permission) {
        if(!(permission instanceof SpacePermission))
            return false;
        String thisName = this.getName();
        String permName = permission.getName();
        if(this.getName().equals("*"))
            return true;
        if(thisName.endsWith("*") && permName.startsWith(thisName.substring(0, thisName.lastIndexOf("*")))) {
            return true;
        }
        if(thisName.equals(permName))
            return true;
        return false;
    }

    public java.security.PermissionCollection newPermissionCollection() {
        return null;
    }

    public String toString() {
        return this.getClass().getName()+","+this.getName();
    }

    public class InnerClass extends java.security.PermissionCollection {

        private java.util.HashMap permissions = new java.util.HashMap();

        public void add(java.security.Permission permission) {
            //required by API
            if(isReadOnly())
                throw new IllegalArgumentException("Read only collection !");
            //must be homogenous collection
            if(!(permission instanceof SpacePermission))
                throw new IllegalArgumentException("Wrong type of permission !");
            //if egzists do nothin
            if(permissions.get(permission.getName())!=null)
                return;
            //if there is no ALLOW ALL permission in set
            if(permissions.get("*") == null) {
                if(permission.getName().equals("*")) {
                    permissions.clear();
                    permissions.put(permission.getName(), permission);
                    return;
                }
                //if adding wildcard remove all weaker permissions
                if(permission.getActions().endsWith("*")) {
                    String wildcarded = permission.getName().substring(0, permission.getName().lastIndexOf("*"));
                    java.util.ArrayList toBeRemoved = new java.util.ArrayList();
                    java.util.Iterator iter = permissions.keySet().iterator();
                    while(iter.hasNext()){
                        String key = (String)iter.next();
                        String permName = ((SpacePermission)permissions.get(key)).getName();
                        if(permName.startsWith(wildcarded)) {
                            toBeRemoved.add(permName);
                        }
                    }
                    for(int i=0; i<toBeRemoved.size(); i++) {
                        permissions.remove(toBeRemoved.get(i));
                    }
                    permissions.put(permission.getName(), permission);
                    toBeRemoved = null;
                    return;
                }
                //if permission without wildcard, look first for wildcarded if not found - add else skip
                java.util.Iterator iter = permissions.keySet().iterator();
                while(iter.hasNext()) {
                    String key = (String)iter.next();
                    if(key.endsWith("*")){
                        String wildcarded = key.substring(0, key.lastIndexOf("*"));
                        if(permission.getName().startsWith(wildcarded))
                            return;
                    }
                }
                permissions.put(permission.getName(), permission);
            }
        }

        public java.util.Enumeration elements() {
            //must use Hashtable becouse of old Collection framework used in this API
            java.util.Hashtable wrapper = new java.util.Hashtable(permissions);
            return wrapper.elements();
        }

        public boolean implies(java.security.Permission permission) {
            if(!(permission instanceof SpacePermission))
                return false;
            if(permissions.get("*") != null)
                return true;
            if(permissions.get(permission.getName())!=null)
                return ((java.security.Permission )permissions.get(permission.getName())).implies(permission);
            java.util.Iterator iter = permissions.keySet().iterator();
            while(iter.hasNext()) {
                String key = (String)iter.next();
                if(key.endsWith("*")){
                    String wildcarded = key.substring(0, key.lastIndexOf("*"));
                    if(permission.getName().startsWith(wildcarded))
                        return ((java.security.Permission)permissions.get(key)).implies(permission);
                }
            }
            return false;
        }
    }

}
