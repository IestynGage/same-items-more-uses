package com.same.bucket.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class SameBucketMoreUsesDataGenerator implements DataGeneratorEntrypoint {

  @Override
  public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
    var pack = fabricDataGenerator.createPack();

    pack.addProvider(SameBucketMoreUsesRecipeProvider::new);
  }
}