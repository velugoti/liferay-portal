/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.upgrade.v6_1_0;

import com.liferay.portal.image.DLHook;
import com.liferay.portal.image.DatabaseHook;
import com.liferay.portal.image.FileSystemHook;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.image.Hook;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Image;
import com.liferay.portal.security.pacl.PACLClassLoaderUtil;
import com.liferay.portal.service.ImageLocalServiceUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.store.DLStoreUtil;
import com.liferay.portlet.documentlibrary.util.ImageProcessorUtil;

import java.io.InputStream;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergio González
 * @author Miguel Pastor
 */
public class UpgradeImageGallery extends UpgradeProcess {

	public UpgradeImageGallery() throws Exception {
		ClassLoader classLoader = PACLClassLoaderUtil.getPortalClassLoader();

		_sourceHookClassName = FileSystemHook.class.getName();

		if (Validator.isNotNull(PropsValues.IMAGE_HOOK_IMPL)) {
			_sourceHookClassName = PropsValues.IMAGE_HOOK_IMPL;
		}

		_sourceHook = (Hook)classLoader.loadClass(
			_sourceHookClassName).newInstance();
	}

	protected void addDLFileEntry(
			String uuid, long fileEntryId, long groupId, long companyId,
			long userId, String userName, long versionUserId,
			String versionUserName, Timestamp createDate,
			Timestamp modifiedDate, long repositoryId, long folderId,
			String name, String extension, String mimeType, String title,
			String description, String extraSettings, String version, long size,
			int readCount, long smallImageId, long largeImageId,
			long custom1ImageId, long custom2ImageId)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			StringBundler sb = new StringBundler(9);

			sb.append("insert into DLFileEntry (uuid_, fileEntryId, groupId, ");
			sb.append("companyId, userId, userName, versionUserId, ");
			sb.append("versionUserName, createDate, modifiedDate, ");
			sb.append("repositoryId, folderId, name, extension, mimeType, ");
			sb.append("title, description, extraSettings, version, size_, ");
			sb.append("readCount, smallImageId, largeImageId, ");
			sb.append("custom1ImageId, custom2ImageId) values (");
			sb.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
			sb.append("?, ?, ?, ?, ?, ?, ?, ?)");

			String sql = sb.toString();

			ps = con.prepareStatement(sql);

			ps.setString(1, uuid);
			ps.setLong(2, fileEntryId);
			ps.setLong(3, groupId);
			ps.setLong(4, companyId);
			ps.setLong(5, userId);
			ps.setString(6, userName);
			ps.setLong(7, versionUserId);
			ps.setString(8, versionUserName);
			ps.setTimestamp(9, createDate);
			ps.setTimestamp(10, modifiedDate);
			ps.setLong(11, repositoryId);
			ps.setLong(12, folderId);
			ps.setString(13, name);
			ps.setString(14, extension);
			ps.setString(15, mimeType);
			ps.setString(16, title);
			ps.setString(17, description);
			ps.setString(18, extraSettings);
			ps.setString(19, version);
			ps.setLong(20, size);
			ps.setInt(21, readCount);
			ps.setLong(22, smallImageId);
			ps.setLong(23, largeImageId);
			ps.setLong(24, custom1ImageId);
			ps.setLong(25, custom2ImageId);

			ps.executeUpdate();
		}
		finally {
			DataAccess.cleanUp(con, ps);
		}
	}

	protected void addDLFileVersion(
			long fileVersionId, long groupId, long companyId, long userId,
			String userName, Timestamp createDate, long repositoryId,
			long folderId, long fileEntryId, String extension, String mimeType,
			String title, String description, String changeLog,
			String extraSettings, long fileEntryTypeId, String version,
			long size, int status, long statusByUserId, String statusByUserName,
			Timestamp statusDate)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			StringBundler sb = new StringBundler(9);

			sb.append("insert into DLFileVersion (fileVersionId, groupId, ");
			sb.append("companyId, userId, userName, createDate, ");
			sb.append("modifiedDate, repositoryId, folderId, fileEntryId, ");
			sb.append("extension, mimeType, title, description, changeLog, ");
			sb.append("extraSettings, fileEntryTypeId, version, size_, ");
			sb.append("status, statusByUserId, statusByUserName, statusDate) ");
			sb.append("values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
			sb.append("?, ?, ?, ?, ?, ?, ?, ?)");

			String sql = sb.toString();

			ps = con.prepareStatement(sql);

			ps.setLong(1, fileVersionId);
			ps.setLong(2, groupId);
			ps.setLong(3, companyId);
			ps.setLong(4, userId);
			ps.setString(5, userName);
			ps.setTimestamp(6, createDate);
			ps.setTimestamp(7, statusDate);
			ps.setLong(8, repositoryId);
			ps.setLong(9, folderId);
			ps.setLong(10, fileEntryId);
			ps.setString(11, extension);
			ps.setString(12, mimeType);
			ps.setString(13, title);
			ps.setString(14, description);
			ps.setString(15, changeLog);
			ps.setString(16, extraSettings);
			ps.setLong(17, fileEntryTypeId);
			ps.setString(18, version);
			ps.setLong(19, size);
			ps.setInt(20, status);
			ps.setLong(21, statusByUserId);
			ps.setString(22, statusByUserName);
			ps.setTimestamp(23, statusDate);

			ps.executeUpdate();
		}
		finally {
			DataAccess.cleanUp(con, ps);
		}
	}

	protected void addDLFolderEntry(
			String uuid, long folderId, long groupId, long companyId,
			long userId, String userName, Timestamp createDate,
			Timestamp modifiedDate, long repositoryId, long parentFolderId,
			String name, String description, Timestamp lastPostDate)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			StringBundler sb = new StringBundler(5);

			sb.append("insert into DLFolder (uuid_, folderId, groupId, ");
			sb.append("companyId, userId, userName, createDate, ");
			sb.append("modifiedDate, repositoryId, mountPoint, ");
			sb.append("parentFolderId, name, description, lastPostDate) ");
			sb.append("values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			String sql = sb.toString();

			ps = con.prepareStatement(sql);

			ps.setString(1, uuid);
			ps.setLong(2, folderId);
			ps.setLong(3, groupId);
			ps.setLong(4, companyId);
			ps.setLong(5, userId);
			ps.setString(6, userName);
			ps.setTimestamp(7, createDate);
			ps.setTimestamp(8, modifiedDate);
			ps.setLong(9, repositoryId);
			ps.setBoolean(10, false);
			ps.setLong(11, parentFolderId);
			ps.setString(12, name);
			ps.setString(13, description);
			ps.setTimestamp(14, lastPostDate);

			ps.executeUpdate();
		}
		finally {
			DataAccess.cleanUp(con, ps);
		}
	}

	protected void deleteConflictingIGPermissions(
			String igResourceName, String dlResourceName)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			DatabaseMetaData databaseMetaData = con.getMetaData();

			boolean supportsBatchUpdates =
				databaseMetaData.supportsBatchUpdates();

			ps = con.prepareStatement(
				"select companyId, scope, primKey, roleId from " +
					"ResourcePermission where name = ?");

			ps.setString(1, igResourceName);

			rs = ps.executeQuery();

			ps = con.prepareStatement(
				"delete from ResourcePermission where name = ? and " +
					"companyId = ? and scope = ? and primKey = ? and " +
						"roleId = ?");

			int count = 0;

			while (rs.next()) {
				ps.setString(1, dlResourceName);
				ps.setLong(2, rs.getLong("companyId"));
				ps.setInt(3, rs.getInt("scope"));
				ps.setString(4, rs.getString("primKey"));
				ps.setLong(5, rs.getLong("roleId"));

				if (supportsBatchUpdates) {
					ps.addBatch();

					if (count == PropsValues.HIBERNATE_JDBC_BATCH_SIZE) {
						ps.executeBatch();

						count = 0;
					}
					else {
						count++;
					}
				}
				else {
				 	ps.executeUpdate();
				}
			}

			if (supportsBatchUpdates && (count > 0)) {
				ps.executeBatch();
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	@Override
	protected void doUpgrade() throws Exception {
		updateIGFolderEntries();
		updateIGImageEntries();
		updateIGFolderPermissions();
		updateIGImagePermissions();

		migrateImageFiles();

		UpgradeDocumentLibrary upgradeDocumentLibrary =
			new UpgradeDocumentLibrary();

		upgradeDocumentLibrary.updateSyncs();
	}

	protected Object[] getImage(long imageId) throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select type_, size_ from Image where imageId = " + imageId);

			rs = ps.executeQuery();

			if (rs.next()) {
				String type = rs.getString("type_");
				long size = rs.getInt("size_");

				return new Object[] {type, size};
			}

			return null;
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected void migrateFile(
			long repositoryId, long companyId, String name, Image image)
		throws Exception {

		InputStream is = _sourceHook.getImageAsStream(image);

		byte[] bytes = FileUtil.getBytes(is);

		if (name == null) {
			name = image.getImageId() + StringPool.PERIOD + image.getType();
		}

		if (DLStoreUtil.hasFile(companyId, repositoryId, name)) {
			DLStoreUtil.deleteFile(companyId, repositoryId, name);
		}

		DLStoreUtil.addFile(companyId, repositoryId, name, false, bytes);
	}

	protected void migrateImage(long imageId) throws Exception {
		Image image = ImageLocalServiceUtil.getImage(imageId);

		try {
			migrateFile(0, 0, null, image);
		}
		catch (Exception e) {
			if (_log.isWarnEnabled()) {
				_log.warn("Ignoring exception for image " + imageId, e);
			}

			return;
		}

		_sourceHook.deleteImage(image);
	}

	protected void migrateImage(
			long fileEntryId, long companyId, long groupId, long folderId,
			String name, long smallImageId, long largeImageId,
			long custom1ImageId, long custom2ImageId)
		throws Exception {

		Image largeImage = null;

		if (largeImageId != 0) {
			largeImage = ImageLocalServiceUtil.getImage(largeImageId);

			long repositoryId = DLFolderConstants.getDataRepositoryId(
				groupId, folderId);

			try {
				migrateFile(repositoryId, companyId, name, largeImage);
			}
			catch (Exception e) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Ignoring exception for image " + largeImageId, e);
				}
			}
		}

		long thumbnailImageId = 0;

		if (smallImageId != 0) {
			thumbnailImageId = smallImageId;
		}
		else if (custom1ImageId != 0) {
			thumbnailImageId = custom1ImageId;
		}
		else if (custom2ImageId != 0) {
			thumbnailImageId = custom2ImageId;
		}

		Image thumbnailImage = null;

		if (thumbnailImageId != 0) {
			thumbnailImage = ImageLocalServiceUtil.getImage(thumbnailImageId);

			Connection con = null;
			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				InputStream is = _sourceHook.getImageAsStream(thumbnailImage);

				con = DataAccess.getUpgradeOptimizedConnection();

				ps = con.prepareStatement(
					"select max(fileVersionId) from DLFileVersion where " +
						"fileEntryId = " + fileEntryId);

				rs = ps.executeQuery();

				if (rs.next()) {
					long fileVersionId = rs.getLong(1);

					ImageProcessorUtil.storeThumbnail(
						companyId, groupId, fileEntryId, fileVersionId,
						custom1ImageId, custom2ImageId, is,
						thumbnailImage.getType());
				}
			}
			catch (Exception e) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Ignoring exception for image " + thumbnailImageId, e);
				}
			}
			finally {
				DataAccess.cleanUp(con, ps, rs);
			}
		}

		if (largeImageId != 0) {
			_sourceHook.deleteImage(largeImage);

			runSQL("delete from Image where imageId = " + largeImageId);
		}

		if ((largeImageId != thumbnailImageId) && (thumbnailImageId != 0)) {
			_sourceHook.deleteImage(thumbnailImage);

			runSQL("delete from Image where imageId = " + thumbnailImageId);
		}
	}

	protected void migrateImageFiles() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			StringBundler sb = new StringBundler(8);

			sb.append("select fileEntryId, companyId, groupId, folderId, ");
			sb.append("name, smallImageId, largeImageId, custom1ImageId, ");
			sb.append("custom2ImageId from DLFileEntry where ((smallImageId ");
			sb.append("is not null) and (smallImageId != 0)) or ");
			sb.append("((largeImageId is not null) and (largeImageId != 0)) ");
			sb.append("or ((custom1ImageId is not null) and (custom1ImageId ");
			sb.append("!= 0)) or ((custom2ImageId is not null) and ");
			sb.append("(custom2ImageId != 0))");

			ps = con.prepareStatement(sb.toString());

			rs = ps.executeQuery();

			while (rs.next()) {
				long fileEntryId = rs.getLong("fileEntryId");
				long companyId = rs.getLong("companyId");
				long groupId = rs.getLong("groupId");
				long folderId = rs.getLong("folderId");
				String name = rs.getString("name");
				long smallImageId = rs.getLong("smallImageId");
				long largeImageId = rs.getLong("largeImageId");
				long custom1ImageId = rs.getLong("custom1ImageId");
				long custom2ImageId = rs.getLong("custom2ImageId");

				migrateImage(
					fileEntryId, companyId, groupId, folderId, name,
					smallImageId, largeImageId, custom1ImageId, custom2ImageId);
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}

		if (!_sourceHookClassName.equals(DLHook.class.getName())) {
			try {
				con = DataAccess.getUpgradeOptimizedConnection();

				ps = con.prepareStatement("select imageId from Image");

				rs = ps.executeQuery();

				while (rs.next()) {
					long imageId = rs.getLong("imageId");

					migrateImage(imageId);
				}
			}
			finally {
				DataAccess.cleanUp(con, ps, rs);
			}

			if (_sourceHookClassName.equals(DatabaseHook.class.getName())) {
				runSQL("update Image set text_ = ''");
			}
		}
	}

	protected void updateIGFolderEntries() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select * from IGFolder order by folderId asc");

			rs = ps.executeQuery();

			Map<Long, Long> folderIds = new HashMap<Long, Long>();

			while (rs.next()) {
				String uuid = rs.getString("uuid_");
				long folderId = rs.getLong("folderId");
				long groupId = rs.getLong("groupId");
				long companyId = rs.getLong("companyId");
				long userId = rs.getLong("userId");
				String userName = rs.getString("userName");
				Timestamp createDate = rs.getTimestamp("createDate");
				Timestamp modifiedDate = rs.getTimestamp("modifiedDate");
				long parentFolderId = rs.getLong("parentFolderId");
				String name = rs.getString("name");
				String description = rs.getString("description");

				if (folderIds.containsKey(parentFolderId)) {
					parentFolderId = folderIds.get(parentFolderId);
				}

				boolean update = updateIGImageFolderId(
					groupId, name, parentFolderId, folderId, folderIds);

				if (!update) {
					addDLFolderEntry(
						uuid, folderId, groupId, companyId, userId, userName,
						createDate, modifiedDate, groupId, parentFolderId, name,
						description, modifiedDate);
				}
			}

			runSQL("drop table IGFolder");
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected void updateIGFolderPermissions() throws Exception {
		deleteConflictingIGPermissions(
			_IG_FOLDER_CLASS_NAME, DLFolder.class.getName());

		runSQL("update ResourcePermission set name = '" +
			DLFolder.class.getName() +
				"' where name = '" + _IG_FOLDER_CLASS_NAME + "'");
	}

	protected void updateIGImageEntries() throws Exception {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement("select * from IGImage");

			rs = ps.executeQuery();

			while (rs.next()) {
				String uuid = rs.getString("uuid_");
				long imageId = rs.getLong("imageId");
				long groupId = rs.getLong("groupId");
				long companyId = rs.getLong("companyId");
				long userId = rs.getLong("userId");
				String userName = rs.getString("userName");
				Timestamp createDate = rs.getTimestamp("createDate");
				Timestamp modifiedDate = rs.getTimestamp("modifiedDate");
				long folderId = rs.getLong("folderId");
				String title = rs.getString("name");
				String description = rs.getString("description");
				long smallImageId = rs.getLong("smallImageId");
				long largeImageId = rs.getLong("largeImageId");
				long custom1ImageId = rs.getLong("custom1ImageId");
				long custom2ImageId = rs.getLong("custom2ImageId");

				Object[] image = getImage(largeImageId);

				if (image == null) {
					continue;
				}

				String extension = (String)image[0];

				String mimeType = MimeTypesUtil.getContentType(
					"A." + extension);

				String name = String.valueOf(
					increment(DLFileEntry.class.getName()));

				long size = (Long)image[1];

				try {
					addDLFileEntry(
						uuid, imageId, groupId, companyId, userId, userName,
						userId, userName, createDate, modifiedDate, groupId,
						folderId, name, extension, mimeType, title, description,
						StringPool.BLANK, "1.0", size, 0, smallImageId,
						largeImageId, custom1ImageId, custom2ImageId);
				}
				catch (Exception e) {
					title = title.concat(StringPool.SPACE).concat(
						String.valueOf(imageId));

					addDLFileEntry(
						uuid, imageId, groupId, companyId, userId, userName,
						userId, userName, createDate, modifiedDate, groupId,
						folderId, name, extension, mimeType, title, description,
						StringPool.BLANK, "1.0", size, 0, smallImageId,
						largeImageId, custom1ImageId, custom2ImageId);
				}

				addDLFileVersion(
					increment(), groupId, companyId, userId, userName,
					createDate, groupId, folderId, imageId, extension, mimeType,
					title, description, StringPool.BLANK, StringPool.BLANK, 0,
					"1.0", size, 0, userId, userName, modifiedDate);
			}

			runSQL("drop table IGImage");
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}
	}

	protected boolean updateIGImageFolderId(
			long groupId, String name, long parentFolderId, long folderId,
			Map<Long, Long> folderIds)
		throws Exception {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataAccess.getUpgradeOptimizedConnection();

			ps = con.prepareStatement(
				"select folderId from DLFolder where groupId = " + groupId +
					" and parentFolderId = " + parentFolderId +
						" and name = ?");

			ps.setString(1, name);

			rs = ps.executeQuery();

			if (rs.next()) {
				long newFolderId = rs.getLong("folderId");

				runSQL(
					"update IGImage set folderId = " + newFolderId +
						" where folderId = " + folderId);

				folderIds.put(folderId, newFolderId);

				return true;
			}
		}
		finally {
			DataAccess.cleanUp(con, ps, rs);
		}

		return false;
	}

	protected void updateIGImagePermissions() throws Exception {
		deleteConflictingIGPermissions(
			_IG_IMAGE_CLASS_NAME, DLFileEntry.class.getName());

		runSQL(
			"update ResourcePermission set name = '" +
				DLFileEntry.class.getName() + "' where name = '" +
					_IG_IMAGE_CLASS_NAME + "'");
	}

	private static final String _IG_FOLDER_CLASS_NAME =
		"com.liferay.portlet.imagegallery.model.IGFolder";

	private static final String _IG_IMAGE_CLASS_NAME =
		"com.liferay.portlet.imagegallery.model.IGImage";

	private static Log _log = LogFactoryUtil.getLog(UpgradeImageGallery.class);

	private Hook _sourceHook;
	private String _sourceHookClassName;

}