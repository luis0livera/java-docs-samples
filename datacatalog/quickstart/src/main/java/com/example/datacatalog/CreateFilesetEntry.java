/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.datacatalog;

// [START datacatalog_create_fileset_quickstart_tag]

import com.google.api.gax.rpc.AlreadyExistsException;
import com.google.api.gax.rpc.NotFoundException;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.cloud.datacatalog.v1.ColumnSchema;
import com.google.cloud.datacatalog.v1.CreateEntryGroupRequest;
import com.google.cloud.datacatalog.v1.CreateEntryRequest;
import com.google.cloud.datacatalog.v1.DataCatalogClient;
import com.google.cloud.datacatalog.v1.Entry;
import com.google.cloud.datacatalog.v1.EntryGroup;
import com.google.cloud.datacatalog.v1.EntryGroupName;
import com.google.cloud.datacatalog.v1.EntryName;
import com.google.cloud.datacatalog.v1.EntryType;
import com.google.cloud.datacatalog.v1.GcsFilesetSpec;
import com.google.cloud.datacatalog.v1.LocationName;
import com.google.cloud.datacatalog.v1.Schema;
import java.io.IOException;

public class CreateFilesetEntry {

  public static void createEntry() {
    // TODO(developer): Replace these variables before running the sample.
    String projectId = "my-project-id";
    String entryGroupId = "fileset_entry_group";
    String entryId = "fileset_entry_id";
    createEntry(projectId, entryGroupId, entryId);
  }

  // Create Fileset Entry.
  public static void createEntry(String projectId, String entryGroupId, String entryId) {
    // Currently, Data Catalog stores metadata in the us-central1 region.
    String location = "us-central1";

    // Initialize client that will be used to send requests. This client only needs to be created
    // once, and can be reused for multiple requests. After completing all of your requests, call
    // the "close" method on the client to safely clean up any remaining background resources.
    try (DataCatalogClient dataCatalogClient = DataCatalogClient.create()) {

      // 1. Environment cleanup: delete pre-existing data.
      // Delete any pre-existing Entry with the same name
      // that will be used in step 3.
      try {
        dataCatalogClient.deleteEntry(
            EntryName.of(projectId, location, entryGroupId, entryId).toString());
      } catch (PermissionDeniedException | NotFoundException e) {
        // PermissionDeniedException or NotFoundException are thrown if
        // Entry does not exist.
        System.out.println("Entry does not exist.");
      }

      // Delete any pre-existing Entry Group with the same name
      // that will be used in step 2.
      try {
        dataCatalogClient.deleteEntryGroup(
            EntryGroupName.of(projectId, location, entryGroupId).toString());
      } catch (PermissionDeniedException | NotFoundException e) {
        // PermissionDeniedException or NotFoundException are thrown if
        // Entry Group does not exist.
        System.out.println("Entry Group does not exist.");
      }

      // 2. Create an Entry Group.
      // Construct the EntryGroup for the EntryGroup request.
      EntryGroup entryGroup =
          EntryGroup.newBuilder()
              .setDisplayName("My Fileset Entry Group")
              .setDescription("This Entry Group consists of ....")
              .build();

      // Construct the EntryGroup request to be sent by the client.
      CreateEntryGroupRequest entryGroupRequest =
          CreateEntryGroupRequest.newBuilder()
              .setParent(LocationName.of(projectId, location).toString())
              .setEntryGroupId(entryGroupId)
              .setEntryGroup(entryGroup)
              .build();

      // Use the client to send the API request.
      EntryGroup entryGroupResponse = dataCatalogClient.createEntryGroup(entryGroupRequest);

      System.out.printf("\nEntry Group created with name: %s\n", entryGroupResponse.getName());

      // 3. Create a Fileset Entry.
      // Construct the Entry for the Entry request.
      Entry entry =
          Entry.newBuilder()
              .setDisplayName("My Fileset")
              .setDescription("This fileset consists of ....")
              .setGcsFilesetSpec(
                  GcsFilesetSpec.newBuilder().addFilePatterns("gs://cloud-samples-data/*").build())
              .setSchema(
                  Schema.newBuilder()
                      .addColumns(
                          ColumnSchema.newBuilder()
                              .setColumn("first_name")
                              .setDescription("First name")
                              .setMode("REQUIRED")
                              .setType("STRING")
                              .build())
                      .addColumns(
                          ColumnSchema.newBuilder()
                              .setColumn("last_name")
                              .setDescription("Last name")
                              .setMode("REQUIRED")
                              .setType("STRING")
                              .build())
                      .addColumns(
                          ColumnSchema.newBuilder()
                              .setColumn("addresses")
                              .setDescription("Addresses")
                              .setMode("REPEATED")
                              .setType("RECORD")
                              .addSubcolumns(
                                  ColumnSchema.newBuilder()
                                      .setColumn("city")
                                      .setDescription("City")
                                      .setMode("NULLABLE")
                                      .setType("STRING")
                                      .build())
                              .addSubcolumns(
                                  ColumnSchema.newBuilder()
                                      .setColumn("state")
                                      .setDescription("State")
                                      .setMode("NULLABLE")
                                      .setType("STRING")
                                      .build())
                              .build())
                      .build())
              .setType(EntryType.FILESET)
              .build();

      // Construct the Entry request to be sent by the client.
      CreateEntryRequest entryRequest =
          CreateEntryRequest.newBuilder()
              .setParent(entryGroupResponse.getName())
              .setEntryId(entryId)
              .setEntry(entry)
              .build();

      // Use the client to send the API request.
      Entry entryResponse = dataCatalogClient.createEntry(entryRequest);

      System.out.printf("\nEntry created with name: %s\n", entryResponse.getName());
    } catch (AlreadyExistsException | IOException e) {
      // AlreadyExistsException's are thrown if EntryGroup or Entry already exists.
      // IOException's are thrown when unable to create the DataCatalogClient,
      // for example an invalid Service Account path.
      System.out.println("Error in create entry process:\n" + e.toString());
    }
  }
}
// [END datacatalog_create_fileset_quickstart_tag]
