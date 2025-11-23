package org.pionerds.ftc.teamcode.Hardware;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;

public class Vision {

    private static final boolean USE_WEBCAM = true; // true for webcam, false for phone camera
    private final DistanceUnit DISTANCE_UNIT = DistanceUnit.CM;
    private final double MULTIPLY_TO_DISTANCE = 0.5;
    Artifact[] artifactAlgorithm = new Artifact[3];
    private boolean obeliskIdentified = false;
    /**
     * The variable to store our instance of the AprilTag processor.
     */
    private AprilTagProcessor aprilTag;
    private Hardware hardware;
    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal;

    public void init(Hardware hardware) {
        initAprilTag(hardware);
    }

    /**
     * Initializes the AprilTag processor.
     *
     * @param hardware The hardware instance to use.
     */

    public void initAprilTag(Hardware hardware) {
        this.hardware = hardware;

        // Create the AprilTag processor.
        aprilTag = new AprilTagProcessor.Builder()
            // The following default settings are available to un-comment and edit as needed.
            .setDrawAxes(true)
            .setDrawCubeProjection(true)
            .setDrawTagOutline(true)
            //.setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
            .setTagLibrary(AprilTagGameDatabase.getDecodeTagLibrary())
            .setOutputUnits(DISTANCE_UNIT, AngleUnit.DEGREES)
            // == CAMERA CALIBRATION ==
            // If you do not manually specify calibration parameters, the SDK will attempt
            // to load a predefined calibration for your camera.
            //.setLensIntrinsics(578.272, 578.272, 402.145, 221.506)
            // ... these parameters are fx, fy, cx, cy.

            .build();

        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // eg: Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second (default)
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second (default)
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        //aprilTag.setDecimation(3);

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera (webcam vs. built-in RC phone camera).
        if (USE_WEBCAM) {
            builder.setCamera(hardware.mapping.getWebcam("Webcam 1"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }

        // Choose a camera resolution. Not all cameras support all resolutions.
        //builder.setCameraResolution(new Size(640, 480));

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        builder.enableLiveView(true);

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        //builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        //builder.setAutoStopLiveView(false);

        // Set and enable the processor.
        builder.addProcessor(aprilTag);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        visionPortal.resumeLiveView();

        // Disable or re-enable the aprilTag processor at any time.
        //visionPortal.setProcessorEnabled(aprilTag, true);
    } // end method initAprilTag()

    // Deprecated.
    public AprilTagPoseFtc getTagPosition(AprilTagNames tagName) {
        for (AprilTagDetection detection : currentDetections()) {
            if (
                !(detection == null) &&
                    !(detection.metadata == null) &&
                    detection.metadata.name.equals(tagName.name())
            ) {
                return detection.ftcPose;
            }
        }
        return null;
    }

    /**
     * Initializes the AprilTag processor.
     *
     * @param aprilTagName The name of the AprilTag to initialize.
     */
    public PioNerdAprilTag getPioNerdAprilTag(AprilTagNames aprilTagName) {
        for (AprilTagDetection detection : currentDetections()) {
            if (
                !(detection == null) &&
                    !(detection.metadata == null) &&
                    detection.metadata.name.equals(aprilTagName.name())
            ) {
                return new PioNerdAprilTag(detection); // Return the PioNerdAprilTag object.
            }
        }
        return null;
    }

    /**
     * Get current April Tag detections.
     *
     * @return ArrayList of AprilTagDetection objects.
     */

    public ArrayList<AprilTagDetection> currentDetections() {
        if (aprilTag.getDetections() == null) {
            return new ArrayList<AprilTagDetection>();
        }
        return aprilTag.getDetections();
    }

    /**
     * Get current April Tag metadata.
     *
     * @return ArrayList of AprilTagMetadata objects.
     */
    public ArrayList<AprilTagMetadata> currentDetectionsMetadata() {
        ArrayList<AprilTagMetadata> detectionNames = new ArrayList<
            AprilTagMetadata
            >();
        List<AprilTagDetection> currentDetections = this.currentDetections();

        // iterate and get names
        for (AprilTagDetection detection : this.currentDetections()) {
            if (detection != null && detection.metadata != null) {
                detectionNames.add(detection.metadata);
            }
        }

        return detectionNames;
    }

    public Artifact[] getCachedValueStrict(){
        return artifactAlgorithm;
    }

    /**
     * Get current artifact pattern.
     *
     * @return Artifact[] array of artifact pattern.
     */

    public Artifact[] getArtifactPattern() {
        if (!obeliskIdentified) {
            ArrayList<AprilTagMetadata> currentDetectionMetadata =
                currentDetectionsMetadata();


            for (AprilTagMetadata metadata : currentDetectionMetadata) {
                switch (metadata.name) {
                    case "Obelisk_GPP":
                        artifactAlgorithm[0] = Artifact.GREEN;
                        artifactAlgorithm[1] = Artifact.PURPLE;
                        artifactAlgorithm[2] = Artifact.PURPLE;
                        obeliskIdentified = true;

                        return artifactAlgorithm;
                    case "Obelisk_PGP":
                        artifactAlgorithm[0] = Artifact.PURPLE;
                        artifactAlgorithm[1] = Artifact.GREEN;
                        artifactAlgorithm[2] = Artifact.PURPLE;
                        obeliskIdentified = true;

                        return artifactAlgorithm;
                    case "Obelisk_PPG":
                        artifactAlgorithm[0] = Artifact.PURPLE;
                        artifactAlgorithm[1] = Artifact.PURPLE;
                        artifactAlgorithm[2] = Artifact.GREEN;
                        obeliskIdentified = true;

                        return artifactAlgorithm;
                    default:
                        obeliskIdentified = false;
                        break;
                }
            }

            // Set default (MOST PROBABLE POINTS!)
            artifactAlgorithm[0] = Artifact.PURPLE;
            artifactAlgorithm[1] = Artifact.PURPLE;
            artifactAlgorithm[2] = Artifact.PURPLE;

            // DO NOT UPDATE OBELISK IDENTIFIED
        }
        return artifactAlgorithm;
    }

    /**
     * Check if the obelisk is identified.
     *
     * @return true if the obelisk is identified, false otherwise.
     */

    public boolean getObeliskIdentified() {
        return obeliskIdentified;
    }

    /**
     * Control the vision portal.
     *
     * @param command the command to execute.
     */
    public void controlVisionPortal(VisionCommands command) {
        switch (command) {
            case RESUME:
                visionPortal.resumeStreaming();
                break;
            case CLOSE:
                visionPortal.close();
                break;
            case STOP:
                visionPortal.stopLiveView();
                break;
            case PAUSE:
                visionPortal.stopStreaming();
                break;
        }
    }

    /**
     * Print the distance of the AprilTag to the telemetry.
     *
     * @param aprilTag the AprilTag to print the distance of.
     */
    public void printTagDistanceToTelemetry(AprilTagNames aprilTag) {
        PioNerdAprilTag blueTargetAprilTag;

        blueTargetAprilTag = getPioNerdAprilTag(aprilTag);

        if (blueTargetAprilTag != null) {
            hardware.telemetry.addLine(
                "\n" +
                    aprilTag.name() +
                    "\nDistances" +
                    "\nx: " +
                    blueTargetAprilTag.x(2) +
                    "\ny: " +
                    blueTargetAprilTag.y(2) +
                    "\nz: " +
                    blueTargetAprilTag.z(2) +
                    "\nRange: " +
                    blueTargetAprilTag.range(2)
            );
        }
    }
}
