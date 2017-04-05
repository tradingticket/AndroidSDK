-printmapping out.map

-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated, SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keep public class it.trade.tradeitsdk.** {
      public protected *;
}

-keepclassmembernames class it.trade.tradeitsdk.** {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

-keepclasseswithmembernames,includedescriptorclasses class it.trade.tradeitsdk.** {
    native <methods>;
}

-keepclassmembers,allowoptimization enum it.trade.tradeitsdk.** {
    public static **[] values(); public static ** valueOf(java.lang.String);
}

-keepclassmembers class it.trade.tradeitsdk.** implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
