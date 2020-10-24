const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

// Listen to event changes (new document added) in 'notifcations' collection
exports.addNewNotification = functions.region('asia-southeast2').firestore.document('notifications/{notification}').onCreate((docSnapshot, context) => {
	console.log('Executing triggerNewNotification() function');
	const message = docSnapshot.data();
	const documentID = docSnapshot.id;
	const deviceToken = message['senderDeviceToken'];

	const payload = {
		notification: {
			title: message['title'],
			body: message['message'],
		},
		data: {
			title: message['title'],
			message: message['message'],
			reminderDate: String(message['reminderDate']),
		}
	}
	
	// call FCM to send notification based on topic
	return admin.messaging().sendToDevice(deviceToken, payload)
	.then(response => {
		console.log('Sending FCM for document ID: ' + documentID + ', deviceToken: ' + deviceToken);
		return null;
	})
	.catch(error => {
		console.error('ERROR sending FCM: ', error);
		return false;
	})
})