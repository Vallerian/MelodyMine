"use client"
import {MeshDistortMaterial, OrbitControls, Sphere} from "@react-three/drei";
import {Canvas} from "@react-three/fiber";


const Hero3D = () => {
    return (
        <Canvas
            style={{
                width: "100%",
                height: "100vh",
                overflow: "hidden"
            }}>
            <OrbitControls enableZoom={false}/>
            <ambientLight intensity={1}/>
            <directionalLight position={[3, 2, 1]}/>
            <Sphere args={[1, 100, 200]} scale={2.2}>
                <MeshDistortMaterial
                    color="#D3D3D3FF"
                    attach="material"
                    distort={0.5}
                    speed={2}
                />
            </Sphere>
        </Canvas>
    )
}
export default Hero3D
