# File Compressor Using Huffman Coding

This project is a file compressor and decompressor using Huffman coding, implemented in Java with a Swing-based graphical user interface (GUI). The application efficiently reduces file sizes by encoding data using Huffman coding, a well-known lossless compression algorithm.

## Features

- **Compress Files**: Reduce the size of files using Huffman coding.
- **Decompress Files**: Restore compressed files to their original state.
- **User-Friendly GUI**: Interactive interface for loading, compressing, and decompressing files.
- **Progress Feedback**: Visual progress bar and status updates during compression and decompression.

## How It Works

Huffman coding is a compression technique that assigns variable-length codes to characters based on their frequencies. Characters that occur more frequently are assigned shorter codes, resulting in overall reduced file sizes.

### Key Components

1. **Node**: Represents a node in the Huffman tree with a character, frequency, and references to left and right children.
2. **HuffmanCoding**: Handles the creation of the Huffman tree, encoding, and decoding of files.
3. **FileUtils**: Provides utility methods for reading and writing binary files, and generating compressed and decompressed file names.
4. **FileCompressorGUI**: Manages the graphical user interface, handling user interactions and displaying progress.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) installed on your machine.
- Git for cloning the repository (optional).

### Installation

1. **Clone the repository**:
    ```bash
    git clone https://github.com/your-username/file-compressor.git
    ```
2. **Navigate to the project directory**:
    ```bash
    cd file-compressor
    ```

### Running the Application

1. **Compile the project**:
    ```bash
    javac *.java
    ```
2. **Run the GUI**:
    ```bash
    java FileCompressorGUI
    ```

## Usage

1. **Load a File**: Click the "Load File" button and select a file to compress.
2. **Compress the File**: Click the "Compress File" button to compress the loaded file. The compressed file will be saved with "compressed" appended to its name.
3. **Decompress a File**: Click the "Decompress File" button and select a compressed file to decompress. The decompressed file will be saved with "decompressed" appended to its name.

## Example

- **Original File**: `example.txt`
- **Compressed File**: `example compressed.txt`
- **Decompressed File**: `example decompressed.txt`

## Screenshots
Screenshots are attached in pdf

For optimal user experience,do not expand the GUI window
